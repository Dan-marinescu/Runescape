package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;


import javax.swing.*;
import javax.swing.plaf.synth.SynthStyle;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "aFishing",
        description = "fishing",
        properties = "client = 4;"
)
public class Fishing extends PollingScript<ClientContext> {
    final int [] fishSpotID = {1527,1530,1528};
    final int [] uniques = {1621,1623,23442,1619};
    final int [] fishesID = {317,321,335,331,339,341,327,345,349};

    int totalfishes=0;
    int fishesAtm =0;
    int startingXp;
    int randomRun = 5 ;
    Tile fishlocation =Tile.NIL;

    String action = "";

    Npc fishingSpot = ctx.npcs.select().id(fishSpotID).poll();

    Random random = new Random();



    public void start(){
        startingXp = ctx.skills.experience(Constants.SKILLS_FISHING);
        String typeOfAction[] = {"Net", "Bait", "Lure"};
        String userItemTypeChoice =""+ JOptionPane.showInputDialog(null,"type of action?","Fishing", JOptionPane.PLAIN_MESSAGE,null,typeOfAction,typeOfAction[0]);
        if(userItemTypeChoice.equals("Net")) {
            action = "Net";
        }
        else if(userItemTypeChoice.equals("Bait")){
            action = "Bait";
        }else{
            action = "Lure";
        }

    }

    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {

            return;
        }

        switch(state){
            case FISH:
                if(ctx.npcs.select().id(fishSpotID).nearest().poll().inViewport()) {
                    fishingSpot = ctx.npcs.select().id(fishSpotID).nearest().poll();
                    fishlocation = fishingSpot.tile();

                    fishingSpot.interact(false,action);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().animation() != -1;
                        }
                    },200,10);
                }
                else{
                    ctx.movement.step(ctx.npcs.select().id(fishSpotID).nearest().poll());
                    ctx.camera.turnTo(ctx.npcs.select().id(fishSpotID).nearest().poll());
                }
                break;

            case DROP:
                fishesAtm =ctx.inventory.select().id(fishesID).count();
                for(Item t:ctx.inventory.select().id(fishesID)){
                    final int startingFish = ctx.inventory.select().id(fishesID).count();
                    ctx.inventory.drop(t,true);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(fishesID).count()!= startingFish;
                        }
                    },25,20);
                }

                for (Item u:ctx.inventory.select().id(uniques))
                {
                    final int startingUniques = ctx.inventory.select().id(uniques).count();
                    ctx.inventory.drop(u,true);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(uniques).count()!= startingUniques;
                        }
                    },25,20);
                }

                totalfishes +=fishesAtm-ctx.inventory.select().id(fishesID).count();
                System.out.println("total fishes:"+ totalfishes);
                break;
        }
    }

    private State getState() {
        if (!ctx.movement.running() && ctx.movement.energyLevel() > randomRun) {
            ctx.movement.running(true);
            randomRun = random.nextInt(5)+8;
        }
        if(ctx.npcs.select().at(fishlocation).id(fishSpotID).poll().equals(ctx.npcs.nil())||(ctx.players.local().animation() ==-1&&ctx.inventory.select().count()<=27))
            return State.FISH;
        else if(ctx.inventory.select().count()>27)
            return State.DROP;
        else
            return null;

    }


    private enum State{
        FISH,DROP
    }


}
//svg
