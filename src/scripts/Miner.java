package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;


import javax.swing.*;
import javax.swing.plaf.synth.SynthStyle;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "aMiner",
        description = "mine",
        properties = "client = 4;"
)
public class Miner extends PollingScript<ClientContext> implements PaintListener{
    final int [] uniques = {1621,1623,23442,1619,20364,20358,1617};

    int [] ROCK_ID = new int [3];

    int min =0;
    int i=0;
    int totalRocks=0;
    int worldsHopped =0;
    int currentWorld =-1;
    int startXp;
    int ORE_ID;
    int randomRun =10;

    Tile rockLocation =Tile.NIL;

    Random random = new Random();

    boolean hopFlag = false;

    final Component inv = ctx.widgets.component(161,56);

    GameObject rocks = null ;

    java.util.Random randomNumber = new java.util.Random();

    public void start(){
        startXp = ctx.skills.experience(Constants.SKILLS_MINING);
        String typeOfItem[] = {"Tin","Cooper","Iron","Coal"};
        String userItemTypeChoice =""+ JOptionPane.showInputDialog(null,"what type of ores?","Mining", JOptionPane.PLAIN_MESSAGE,null,typeOfItem,typeOfItem[0]);
        if(userItemTypeChoice.equals("Tin")) {
            ORE_ID = 438;
            ROCK_ID[0] = 11360;
            ROCK_ID[1] = 11361;
            ROCK_ID[2] = 10080;
        }
        else if(userItemTypeChoice.equals("Cooper")) {
            ORE_ID = 436;
            ROCK_ID[0] = 11161;
            ROCK_ID[1] = 10943;
        }
        else if(userItemTypeChoice.equals("Iron")) {
            ORE_ID = 440;
            ROCK_ID[0] = 11364;
            ROCK_ID[1] = 11365;
        }
        else {
            ORE_ID = 453;
            ROCK_ID [0] = 11366;
            ROCK_ID [1] = 11367;
        }
        rocks = ctx.objects.select().id(ROCK_ID).poll();
    }

    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {
            return;
        }

        switch(state){
            case MINE:
                System.out.println("state is:"+state);
                rocks = ctx.objects.select().id(ROCK_ID).nearest().poll();
                rockLocation = rocks.tile();
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

            case DROP:
                System.out.println("state is:"+state);
                min=0;
                inv.click();
                for(Item t:ctx.inventory.select().id(ORE_ID)){
                    final int startingTins = ctx.inventory.select().id(ORE_ID).count();
                    if(ctx.inventory.shiftDroppingEnabled())
                        ctx.inventory.drop(t,true);
                    else
                        ctx.inventory.drop(t,false);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(ORE_ID).count()!= startingTins;
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
                break;

                case HOP:
                    System.out.println("state is:"+state);
                    currentWorld = Integer.parseInt(ctx.widgets.component(429,3).text().replaceAll("\\D",""));
                    World joinW = ctx.worlds.select().types(World.Type.FREE).joinable().shuffle().peek();
                    if(!joinW.hop())
                    {   System.out.println("waiting for hop cooldown.");
                        Condition.sleep(org.powerbot.script.Random.nextInt(3500, 5000));
                    }
                    hopFlag=false;
                    worldsHopped++;
                    inv.click();
                break;
        }
    }

    private State getState() {
        if (!ctx.movement.running() && ctx.movement.energyLevel() > randomRun) {
            ctx.movement.running(true);
            randomRun = random.nextInt(5)+8;
        }
       /* if(hopFlag||ctx.players.select().within(28).size()>4)
        {
            System.out.println("hopflag is:"+hopFlag+" peoples:"+ctx.players.select().within(28).size());
            return State.HOP;
        }*/
        if(ctx.objects.select().at(rockLocation).id(ROCK_ID).poll().equals(ctx.objects.nil())||ctx.players.local().animation() == -1 && !ctx.inventory.isFull())
            return State.MINE;
        else if(ctx.inventory.isFull())
            return State.DROP;
        else
            return null;
    }

    private enum State{
        MINE,DROP,HOP
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


}
