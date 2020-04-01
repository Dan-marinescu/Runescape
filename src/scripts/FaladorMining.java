package scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Callable;


@Script.Manifest(
        name = "Coal miner",
        description = "Mine coals and bank in falador mining guide",
        properties = "client = 4;"
)


public class FaladorMining extends PollingScript<ClientContext> implements PaintListener {
    final int [] uniques = {1621,1623,23442,1619,20364,20358,1617};
    final int [] PICKAXES_ID = {1267,1269,1271,1273,1275,12297};

    public static final Tile[] pathToMine = {new Tile(3013, 3356, 0), new Tile(3016, 3359, 0), new Tile(3020, 3359, 0), new Tile(3022, 3355, 0), new Tile(3025, 3351, 0), new Tile(3028, 3348, 0), new Tile(3029, 3344, 0), new Tile(3029, 3340, 0), new Tile(3026, 3337, 0), new Tile(3022, 3337, 0), new Tile(3021, 9739, 0), new Tile(3025, 9739, 0), new Tile(3029, 9738, 0), new Tile(3033, 9740, 0), new Tile(3037, 9741, 0), new Tile(3041, 9742, 0)};


    int [] ROCK_ID = {11366,11367};

    int min =0;
    int i=0;
    int totalRocks=0;
    int startXp;
    int ORE_ID = 453;
    int randomRun =10;

    Tile rockLocation =Tile.NIL;
    Tile bankTile = new Tile(3013,3356,0);
    Tile mineTile = new Tile(3027,9739,0);


    Random random = new Random();

    GameObject rocks = ctx.objects.select().id(ROCK_ID).poll();

    private final Walker walk = new Walker(ctx);

    java.util.Random randomNumber = new java.util.Random();

    public void start(){
        startXp = ctx.skills.experience(Constants.SKILLS_MINING);
    }

    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {
            return;
        }

        switch(state){
            case MINE:
                System.out.println("state is:" +state);
                if(!rocks.inViewport())
                    ctx.camera.turnTo(rocks);
                rocks = ctx.objects.select().id(ROCK_ID).nearest().poll();
                rockLocation = rocks.tile();
                if(rocks.tile().y()<9728) {
                    ctx.movement.step(mineTile);
                    break;
                }
                rocks.interact(true,"Mine");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() != -1;
                    }
                },200,10);
                if(ctx.inventory.select().id(ORE_ID).count()>min){
                    min=ctx.inventory.select().id(ORE_ID).count();
                    totalRocks++;
                }
                break;

            case BANK:
                System.out.println("state is:" +state);
                if(ctx.bank.inViewport()) {
                    if (ctx.bank.open()) {
                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                System.out.println("waiting for bank open");
                                return ctx.bank.opened();
                            }
                        }, 500, 100);
                    }
                    ctx.bank.depositAllExcept(PICKAXES_ID);
                    min = 0;
                    ctx.bank.close();
                }
                else{
                    ctx.movement.step(ctx.bank.nearest());
                    ctx.camera.turnTo(ctx.bank.nearest());
                }

                break;

            case TO_BANK:
                System.out.println("state is:" +state);
                while(!atArea(bankTile)&&(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5))
                    walk.walkPathReverse(pathToMine);
                break;


            case TO_MINE:
                System.out.println("state is:" +state);
                while((!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5)&&ctx.players.local().tile().y()<9000)
                    walk.walkPath(pathToMine);
                break;

        }
    }

    private State getState() {
        if (!ctx.movement.running() && ctx.movement.energyLevel() > randomRun) {
            ctx.movement.running(true);
            randomRun = random.nextInt(5)+8;
        }
        if (ctx.inventory.isFull() && atArea(bankTile))
            return State.BANK;
        else if(ctx.inventory.isFull())
            return State.TO_BANK;
        else if(!ctx.inventory.isFull() && ctx.players.local().tile().y()<9000)
            return State.TO_MINE;
        else if (ctx.objects.select().at(rockLocation).id(ROCK_ID).poll().equals(ctx.objects.nil())||ctx.players.local().animation() == -1 && !ctx.inventory.isFull())
            return State.MINE;
        else
            return null;
    }

    private enum State{
        MINE,TO_BANK,TO_MINE,BANK
    }

    @Override
    public void repaint(Graphics graphics){
        long milliseconds =this.getTotalRuntime();
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60) % 60);
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        int xpGained = ctx.skills.experience(Constants.SKILLS_MINING)-startXp;
        Graphics2D g =(Graphics2D)graphics;
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0,0,200,150);

        g.setColor(new Color(255,255,255));
        g.drawRect(0,0,200,150);

        g.drawString("Mining by Kaskas",20,20);
        g.drawString("running: "+String.format("%02d:%02d:%02d",hours,minutes,seconds),20,40);
        g.drawString("ores P/H:"+ (int)(totalRocks*(3600000D/milliseconds)),20,60);
        g.drawString("ores mined:"+ totalRocks,20,80);
        g.drawString("xp P/H:"+String.format("%.2f",xpGained*(3600000D/milliseconds)),20,100);

    }

    public boolean atArea(Tile t) { return (ctx.movement.distance(t) >= 1 && ctx.movement.distance(t) <= 5); }



}



