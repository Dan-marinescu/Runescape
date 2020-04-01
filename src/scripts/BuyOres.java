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
        name = "Buy ores",
        description = "Buy iron ores from coal mines",
        properties = "client = 4;"
)


public class BuyOres extends PollingScript<ClientContext> implements PaintListener {
    public static final Tile[] pathToMine = {new Tile(3015, 3355, 0), new Tile(3013, 3359, 0), new Tile(3017, 3359, 0), new Tile(3021, 3358, 0), new Tile(3022, 3354, 0), new Tile(3025, 3351, 0), new Tile(3028, 3348, 0), new Tile(3029, 3344, 0), new Tile(3029, 3340, 0), new Tile(3026, 3337, 0), new Tile(3022, 3337, 0), new Tile(3021, 9739, 0), new Tile(3025, 9739, 0), new Tile(3029, 9740, 0), new Tile(3031, 9744, 0)};

    final Tile bankTile = new Tile(3013,3356,0);

    int randomRun =10;
    int totalRocks = 0;

    final int COINS_ID = 995;

    Random random = new Random();

    private final Walker walk = new Walker(ctx);

    Npc hendor = ctx.npcs.select().name("Hendor").nearest().poll();
    final Component closeWindow = ctx.widgets.component(300,1,3);
    final Component worldSwitcher = ctx.widgets.component(182,7);

    public void start(){

    }

    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {
            return;
        }

        switch(state){
            case BUY:
                System.out.println("a");
                if(!ctx.widgets.component(300,1).valid()) {
                    hendor = ctx.npcs.select().name("Hendor").nearest().poll();
                    System.out.println("b");
                    if (!hendor.inViewport()) {
                        System.out.println("c");
                        ctx.camera.turnTo(hendor);
                        ctx.movement.step(hendor);
                    }
                    hendor.interact(false, "Trade");
                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            System.out.println("d");
                            System.out.println("waiting for trade window");
                            return ctx.widgets.component(300, 1).valid();
                        }
                    }, 200, 20);
                }
                else{

                    if(ctx.widgets.component(300,16,3).itemStackSize()<500){
                        System.out.println("e");
                        closeWindow.click();
                        if (ctx.widgets.component(182, 7).valid())
                            worldSwitcher.click();
                        ctx.worlds.select().types(World.Type.FREE).joinable().shuffle().peek().hop();
                        ctx.input.send("" + 2);
                        break;
                    }
                    else{
                        System.out.println("f");
                        if(ctx.widgets.component(300,16,3).interact(false,"Buy 50"))
                            totalRocks+= 27;
                        closeWindow.click();
                    }
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
                    ctx.bank.depositAllExcept(COINS_ID);
                    ctx.bank.close();
                }
                else{
                    ctx.movement.step(ctx.bank.nearest());
                    ctx.camera.turnTo(ctx.bank.nearest());
                }
                break;

            case TO_BANK:
                System.out.println("state is:" +state);
                if(!atArea(bankTile)&&(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5))
                    walk.walkPathReverse(pathToMine);
                break;


            case TO_MINE:
                System.out.println("state is:" +state);
                if((!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5)&&ctx.players.local().tile().y()<9743)
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
        else if(!ctx.inventory.isFull() && ctx.players.local().tile().y()<9743)
            return State.TO_MINE;
        else if (ctx.npcs.select().name("Hendor").nearest().poll().valid() && !ctx.inventory.isFull())
            return State.BUY;
        else
            return null;
    }

    private enum State{
        BUY,TO_BANK,TO_MINE,BANK
    }

    @Override
    public void repaint(Graphics graphics){
        long milliseconds =this.getTotalRuntime();
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60) % 60);
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        Graphics2D g =(Graphics2D)graphics;
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0,0,200,150);

        g.setColor(new Color(255,255,255));
        g.drawRect(0,0,200,150);

        g.drawString("Mining by Kaskas",20,20);
        g.drawString("running: "+String.format("%02d:%02d:%02d",hours,minutes,seconds),20,40);
        g.drawString("ores P/H:"+ (int)(totalRocks*(3600000D/milliseconds)),20,60);
        g.drawString("ores bought:"+ totalRocks,20,80);
        g.drawString("state is:"+ getState(),20,100);


    }

    public boolean atArea(Tile t) { return (ctx.movement.distance(t) >= 1 && ctx.movement.distance(t) <= 5); }



}



