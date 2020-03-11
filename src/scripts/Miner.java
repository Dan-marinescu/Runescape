package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;


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
    final int [] rocksID = {11365,11364,11361,11360};
    final int [] uniques = {1621,1623,23442,1619,20364,20358};
    final int COOPER_ORE_ID = 436;
    final int []TIN_ORE_ID = {440,438};
    int min =0;
    int miningXpInit = ctx.skills.experience(Constants.SKILLS_MINING);
    int x5=0;
    int i=0;
    int totalRocks=0;
    int invRocks =0;
    int worldsHopped =0;
    int currentWorld =-1;
    Tile rockLocation =Tile.NIL;

    boolean hopFlag = false;
    Item cooper = ctx.inventory.select().id(COOPER_ORE_ID).poll();
    Item tin = ctx.inventory.select().id(TIN_ORE_ID).poll();
    final Component inv = ctx.widgets.component(161,54);
    GameObject rocks = ctx.objects.select().id(rocksID).poll();

    Tile outSide = new Tile(2608,3115,0);
    Tile middle;
    java.util.Random randomNumber = new java.util.Random();
    int startXp;


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
                rocks = ctx.objects.select().id(rocksID).nearest().poll();
                rockLocation = rocks.tile();
                rocks.interact(true,"Mine");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() != -1;
                    }
                },200,10);
                if(ctx.inventory.select().id(TIN_ORE_ID).count()>min){
                    min=ctx.inventory.select().id(TIN_ORE_ID).count();
                    totalRocks++;
                }
                break;

            case DROP:
                System.out.println("state is:"+state);
                min=0;
                inv.click();
                for(Item t:ctx.inventory.select().id(TIN_ORE_ID)){
                    final int startingTins = ctx.inventory.select().id(TIN_ORE_ID).count();
                    ctx.inventory.drop(t,true);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(TIN_ORE_ID).count()!= startingTins;
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
        if((ctx.skills.experience(Constants.SKILLS_MINING)-miningXpInit)>x5){
            x5 +=2500;
            System.out.println("milestone of x5:"+x5 +" hopped:"+worldsHopped);
           // hopFlag =true;
            if (!ctx.movement.running() && ctx.movement.energyLevel() > 20)
                ctx.movement.running(true);
        }
        //System.out.println("hopflag is:"+hopFlag+" peoples:"+ctx.players.select().within(28).size());
        if(hopFlag||ctx.players.select().within(28).size()>4)
        {
            System.out.println("hopflag is:"+hopFlag+" peoples:"+ctx.players.select().within(28).size());
            return State.HOP;
        }
        else if(ctx.objects.select().at(rockLocation).id(rocksID).poll().equals(ctx.objects.nil())||ctx.players.local().animation() ==-1&&ctx.inventory.select().count()<=27)
            return State.MINE;
        else if(ctx.inventory.select().count()>27)
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
