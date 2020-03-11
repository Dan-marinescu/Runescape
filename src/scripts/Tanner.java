package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import z.Con;

import javax.swing.*;
import javax.swing.plaf.synth.SynthStyle;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "AAAATanner",
        description = "tanning hides",
        properties = "client = 4;"
)
public class Tanner extends PollingScript<ClientContext> implements PaintListener{
    String wealthIds = "11980 11982 11984 11986 11988";
    String duelsIds = "2552 2554 2556  2558 2560 2562 2564 2566";
    boolean geFlag = false;
    int LEATHER_ID;
    int TANNED_LEATHER_ID;
    final int COINS_ID = 995;
    int tanIndex;
    int tanned =0;
    int x300 =300;
    public static final Tile[] pathToTan = {new Tile(3269, 3167, 0), new Tile(3273, 3167, 0), new Tile(3275, 3171, 0), new Tile(3277, 3175, 0), new Tile(3277, 3179, 0), new Tile(3280, 3183, 0), new Tile(3280, 3187, 0), new Tile(3279, 3191, 0), new Tile(3275, 3191, 0)};
    public static final Tile[] pathToBank = {new Tile(3315, 3233, 0), new Tile(3311, 3234, 0), new Tile(3307, 3234, 0), new Tile(3303, 3231, 0), new Tile(3300, 3228, 0), new Tile(3297, 3225, 0), new Tile(3297, 3221, 0), new Tile(3294, 3218, 0), new Tile(3291, 3214, 0), new Tile(3289, 3210, 0), new Tile(3286, 3207, 0), new Tile(3283, 3204, 0), new Tile(3282, 3200, 0), new Tile(3282, 3196, 0), new Tile(3282, 3192, 0), new Tile(3281, 3188, 0), new Tile(3281, 3184, 0), new Tile(3281, 3180, 0), new Tile(3280, 3176, 0), new Tile(3277, 3173, 0), new Tile(3275, 3169, 0), new Tile(3271, 3167, 0)};

    private  GrandExchange ge = new GrandExchange(ctx);
    private final Walker walk = new Walker(ctx);

    final Component worldSwitcher = ctx.widgets.component(182,7);
    final Component logoutIcon = ctx.widgets.component(161,45);

    public final Tile shopTile = new Tile(3273,3191,0);
    public final Tile bankTile = new Tile(3270,3167,0);
    public final Tile GeTile = new Tile(3165,3476,0);
    public final Tile duelTile = new Tile(3315,3233,0);
    String userItemTypeChoice;


    private final Component amountComp = ctx.widgets.component(465,24,49);
    private final Component raisePriceComp = ctx.widgets.component(465,24,53);
    private final Component reducePriceComp = ctx.widgets.component(465,24,50);
    private final Component confirmComp = ctx.widgets.component(465,24,54);
    private final Component collectComp = ctx.widgets.component(465,6,1);
    private final Component setPriceComp = ctx.widgets.component(465,24,12);



    public void start() {
        String typeOfItem[] = {"Soft leather", "Hard leather", "Snakeskin1", "Snakeskin2", "Green dragonhide", "Blue dragonhide", "Red dragonhide", "Black dragonhide"};
        userItemTypeChoice = "" + JOptionPane.showInputDialog(null, "what type of item?", "Fleching", JOptionPane.PLAIN_MESSAGE, null, typeOfItem, typeOfItem[0]);
        int x =5;
        if (userItemTypeChoice.equals("Soft leather")) {
            userItemTypeChoice ="cowhide";
            LEATHER_ID = 1739;
            tanIndex = 92;
            TANNED_LEATHER_ID = 1741;
        } else if (userItemTypeChoice.equals("Hard leather")) {
            userItemTypeChoice = "cowhide";
            LEATHER_ID = 1739;
            tanIndex = 93;
            TANNED_LEATHER_ID = 1743;
        } else if (userItemTypeChoice.equals("Snakeskin1")) {
            userItemTypeChoice = "snake hide";
            LEATHER_ID = 7801;
            tanIndex = 94;
            TANNED_LEATHER_ID = 6289;
        } else if (userItemTypeChoice.equals("Snakeskin2")) {
            userItemTypeChoice = "snake hide";
            LEATHER_ID = 6287;
            tanIndex = 95;
            TANNED_LEATHER_ID = 6289;
        } else if (userItemTypeChoice.equals("Green dragonhide")) {
            LEATHER_ID = 1753;
            tanIndex = 96;
            TANNED_LEATHER_ID = 1745;
        } else if (userItemTypeChoice.equals("Blue dragonhide")) {
            LEATHER_ID = 1751;
            tanIndex = 97;
            TANNED_LEATHER_ID = 2505;
        } else if (userItemTypeChoice.equals("Red dragonhide")) {
            LEATHER_ID = 1749;
            tanIndex = 98;
            TANNED_LEATHER_ID = 2507;
        } else {
            userItemTypeChoice = "Black dragonhide";
            LEATHER_ID = 1747;
            tanIndex = 99;
            TANNED_LEATHER_ID = 2509;
        }
    }

    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {
            System.out.println("state is:" +state);
            return;
        }
        switch(state){
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
                    if(ctx.inventory.select().id(COINS_ID).count(true)<3000)
                        ctx.bank.withdraw(COINS_ID,20000);

                    if(ctx.bank.select().id(LEATHER_ID).count(true)==0 && ctx.bank.opened()){
                       // geFlag =true;
                        ctx.controller.stop();
                        System.out.println("here?");

                    }

                    ctx.bank.withdraw(LEATHER_ID,27);
                    ctx.bank.close();
                    if(tanned >x300){
                        x300 +=300;
                        logoutIcon.click();
                        if(ctx.widgets.component(182,7).valid())
                            worldSwitcher.click();
                        ctx.worlds.select().types(World.Type.FREE).joinable().shuffle().joinable().peek().hop();
                    }
                }
                else{
                    ctx.movement.step(ctx.bank.nearest());
                    ctx.camera.turnTo(ctx.bank.nearest());
                }
                break;


            case WALK_TO_LEATHER:
                System.out.println("state is:" +state);
                while(!atArea(shopTile)&&(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5)){
                    walk.walkPath(pathToTan);
                }
                break;

            case WALK_TO_BANK:
                System.out.println("state is:" +state);
                while(!atArea(bankTile)&&(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5))
                    walk.walkPathReverse(pathToTan);
                break;

            case TAN:
                System.out.println("state is:" +state);
                int hides =ctx.inventory.select().id(LEATHER_ID).count();
                ctx.npcs.select().name("Ellis").poll().interact(false, "Trade");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.widgets.component(324,88).visible();
                    }
                },250,10);

                if(ctx.widgets.component(324,88).visible()) {
                 Component tan = ctx.widgets.component(324,tanIndex);
                 tan.interact(false,"Tan All");
                }

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.inventory.select().id(TANNED_LEATHER_ID).count()>0;
                    }
                },50,50);

                if(hides != ctx.inventory.select().id(LEATHER_ID).count())
                    tanned += ctx.inventory.select().id(TANNED_LEATHER_ID).count();

                break;
        }
    }

    private State getState() {
        if (!ctx.movement.running() && ctx.movement.energyLevel() > org.powerbot.script.Random.nextInt(17, 35))
            ctx.movement.running(true);
        if(geFlag){
            goToGe();
            ctx.controller.stop();
        }

        if(atArea(shopTile)&&ctx.inventory.select().id(LEATHER_ID).count()>0)
            return State.TAN;
        else if(ctx.inventory.select().id(LEATHER_ID).count()>0)
            return State.WALK_TO_LEATHER;
        else if(ctx.inventory.select().id(LEATHER_ID).count()==0&&!atArea(bankTile))
            return State.WALK_TO_BANK;
        else if(ctx.inventory.select().id(LEATHER_ID).count()==0)
            return State.BANK;
        return null;
    }

    private enum State{
        BANK,WALK_TO_LEATHER,WALK_TO_BANK,TAN
    }


    public boolean atArea(Tile t) { return (ctx.movement.distance(t) >= 1 && ctx.movement.distance(t) <= 5); }

    public void goToGe(){
        if(ctx.bank.inViewport()) {
            if (ctx.bank.open()) {
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for bank open");
                        return ctx.bank.opened();
                    }
                }, 500, 100);
            }
            ctx.bank.depositInventory();
            System.out.println(itemId("dueling"));
            ctx.bank.withdraw(itemId("dueling"),1);
            ctx.bank.withdraw(itemId("wealth ("),1);
            ctx.bank.withdrawModeNoted(true);
            ctx.bank.withdraw(LEATHER_ID+1,ctx.bank.select().id(LEATHER_ID+1).count(true));
            ctx.inventory.select().id(itemId("wealth (")).poll().interact(false,"Rub");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(219,1,2).text().contains("Grand");
                }
            },100,20);
            ctx.widgets.component(219,1,2).click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return atArea(GeTile);
                }
            },200,10);
            ctx.movement.step(ctx.bank.nearest());
            ctx.camera.turnTo(ctx.bank.nearest());
            ge.sellItem(LEATHER_ID,1600);
            ge.buyItemByPrice(userItemTypeChoice,tanned,1470);
            ctx.inventory.select().id(itemId("dueling")).poll().interact(false,"Rub");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(219,1,1).text().contains("Kharid");
                }
            },100,20);
            ctx.widgets.component(219,1,1).click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return atArea(duelTile);
                }
            },200,10);
            if(atArea(duelTile))
            {
                while(!atArea(bankTile)&&(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5))
                    walk.walkPath(pathToBank);
            }


        }else {
            ctx.movement.step(ctx.bank.nearest());
            ctx.camera.turnTo(ctx.bank.nearest());
        }


    }

    public int itemId(String  ringIds){
        if(!ctx.bank.opened())
            return -1;
        else{
            for(int i=0;i<Integer.parseInt(ctx.widgets.component(12,5).text());i++){
                if(ringIds.contains(Integer.toString(ctx.widgets.component(12,13,i).itemId())))
                    return ctx.widgets.component(12,13,i).itemId();
            }
            return -1;
        }
    }






    @Override
    public void repaint(Graphics graphics){
        long milliseconds =this.getTotalRuntime();
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60) % 60);
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        Graphics2D g =(Graphics2D)graphics;
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0,0,240,160);

        g.setColor(new Color(255,255,255));
        g.drawRect(0,0,240,160);

        g.drawString("Tanning hides by Kaskas",20,20);
        g.drawString("running: "+String.format("%02d:%02d:%02d",hours,minutes,seconds),20,40);
        g.drawString("tans P/H:"+ (int)(tanned*(3600000D/milliseconds)),20,60);
        g.drawString("tans collected:"+ tanned,20,80);
        g.drawString("current state:"+getState(),20,100);
    }

}
