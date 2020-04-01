package scripts;


import org.powerbot.Con;
import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.GameObject;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "Woodcutter",
        description = "Woodcutting trees",
        properties = "client = 4;"
)
public class Woodcutting extends PollingScript<ClientContext> {
    int randomRun = 5 ;
    int startingXp;

    final int [] NORMAL_TREE_ID = {1276,1278};
    final int [] OAK_TREE_ID = {10820};
    int [] CHOSEN_TREE_ID;
    final int [] AXES_ID = {1349,1353,1355,1361,1357};

    final boolean [] randomBoolean = {true,false};

    Random random = new Random();
    public static Tile [] chosenPath;
    public static final Tile[] pathFromOaks = {new Tile(3285, 3427, 0), new Tile(3281, 3427, 0), new Tile(3277, 3427, 0), new Tile(3273, 3427, 0), new Tile(3269, 3427, 0), new Tile(3265, 3427, 0), new Tile(3261, 3427, 0), new Tile(3257, 3428, 0), new Tile(3254, 3425, 0)};

    public static final Tile[] pathFromNormalBank = {new Tile(3282, 3450, 0), new Tile(3282, 3446, 0), new Tile(3280, 3442, 0), new Tile(3277, 3439, 0), new Tile(3275, 3435, 0), new Tile(3274, 3431, 0), new Tile(3270, 3429, 0), new Tile(3266, 3429, 0), new Tile(3262, 3429, 0), new Tile(3258, 3429, 0), new Tile(3254, 3426, 0), new Tile(3254, 3422, 0)};
    Tile treeLocation =Tile.NIL;

    GameObject treeObj;

    private final Walker walk = new Walker(ctx);

    public void start(){
        startingXp = ctx.skills.experience(Constants.SKILLS_WOODCUTTING);
        String typeOfTree[] = {"Normal","Oak"};
        String userItemTypeChoice =""+ JOptionPane.showInputDialog(null,"what type of item?","Fleching", JOptionPane.PLAIN_MESSAGE,null,typeOfTree,typeOfTree[0]);
        if(userItemTypeChoice.equals("Normal")) {
            CHOSEN_TREE_ID = NORMAL_TREE_ID;
            chosenPath = pathFromNormalBank;
        }
        else {
            CHOSEN_TREE_ID = OAK_TREE_ID;
            chosenPath = pathFromOaks;
        }
    }

    @Override
    public void poll() {
        final State state = getState();
        if (state == null) {
            System.out.println("state is:" + state);
            return;
        }

        switch (state) {
            case CHOP:
                System.out.println("state is:"+state);
                treeObj = ctx.objects.select().id(CHOSEN_TREE_ID).nearest().poll();
                if(!treeObj.inViewport()){
                    ctx.camera.turnTo(treeObj);
                }
                else{
                    if(ctx.players.local().animation() == -1){
                    treeLocation = treeObj.tile();
                    treeObj.interact(randomBoolean[random.nextInt(1)],"Chop");
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().animation() != -1;
                        }
                    },200,10);
                }
                }
                break;

            case WALK:
                System.out.println("state is:"+state);
                if(ctx.inventory.isFull()){
                    if(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5)
                        walk.walkPath(chosenPath);
                } else {
                    walk.walkPathReverse(chosenPath);
                }
                break;

            case BANK:
                System.out.println("state is:"+state);
                if(ctx.bank.opened()){
                    ctx.bank.depositAllExcept(AXES_ID);
                }   else
                    if(ctx.bank.inViewport()){
                        if(ctx.bank.open())
                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return ctx.bank.opened();
                                }
                            },200,20);
                    } else {
                        ctx.camera.turnTo(ctx.bank.nearest());
                        ctx.movement.step(ctx.bank.nearest());
                    }
                break;
        }


    }
        private State getState () {
            treeObj = ctx.objects.select().id(CHOSEN_TREE_ID).nearest().poll();
            if (!ctx.movement.running() && ctx.movement.energyLevel() > randomRun) {
                 ctx.movement.running(true);
                 randomRun = random.nextInt(5)+8;
            }
            System.out.println("total xp gained:"+(ctx.skills.experience(Constants.SKILLS_WOODCUTTING)-startingXp) +" "+(chosenPath[0].distanceTo(ctx.players.local())));
            if (ctx.inventory.isFull() && ctx.bank.nearest().tile().distanceTo(ctx.players.local())<6)
                return State.BANK;
            else if((!ctx.objects.select().at(treeLocation).id(CHOSEN_TREE_ID).poll().equals(ctx.objects.nil())||ctx.players.local().animation() == -1) &&( !ctx.inventory.isFull() && (chosenPath[0].distanceTo(ctx.players.local())<20)))
                return State.CHOP;
            else if(ctx.inventory.isFull() || (!ctx.inventory.isFull() && (chosenPath[0].distanceTo(ctx.players.local())>8)))
                return State.WALK;
            else
                return null;

        }



    private enum State {
        BANK,CHOP,WALK
    }

}
