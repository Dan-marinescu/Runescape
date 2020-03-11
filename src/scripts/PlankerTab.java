package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;


import javax.swing.plaf.synth.SynthStyle;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "PlankerTabs",
        description = "making oak planks in PVP with tabs",
        properties = "client = 4;"
)
public class PlankerTab extends PollingScript<ClientContext> {
    public static final int LOGS_ID = 1521;//oak logs 1521
    public static final int PLANKS_ID = 8778;// oak planks 8778
    public static final int NOTED_PLANKS_ID=8779; //noted oak planks 8779
    public static final int LAWS_ID = 563;
    public static final int COINS_ID = 995;
    public static final int BUTLER_ID = 229;
    public static final int WANTED_PLANKS=1000;
    public static final int LUMB_TAB_ID=8008;
    public static final int HOUSE_TAB_ID=8013;
    public static final int CAMMY_TAB_ID=8010;
    public static final String TRADE_WITH = "Kaskas";//maxsloot
    public int totalLogs=100,totalLaws=100,totalCoins=100000,attempts=0,totalHouseTabs=100,totalLumbTabs=100;
    Tile atLumb = new Tile(3221,3219,0);
    Tile atCammy = new Tile(2758,3477,0);
    java.util.Random randomNumber = new java.util.Random();

    Item houseTab = ctx.inventory.select().id(HOUSE_TAB_ID).poll();
    Item lumbTab = ctx.inventory.select().id(LUMB_TAB_ID).poll();
    Item camTab = ctx.inventory.select().id(CAMMY_TAB_ID).poll();
    Item coins = ctx.inventory.select().id(COINS_ID).poll();
    Item notedPlanks = ctx.inventory.select().id(NOTED_PLANKS_ID).poll();

    final Component inv = ctx.widgets.component(161,54);
    final Component compPlanks = ctx.widgets.component(149, 0);
    final Component magicSpellBook = ctx.widgets.component(161,57);
    final Component houseTeleport = ctx.widgets.component(218,27);
    final Component cammyTeleport = ctx.widgets.component(218,30);
    final Component lumbTeleport = ctx.widgets.component(218,22);
    final Component setting = ctx.widgets.component(161,39);
    final Component houseOptions = ctx.widgets.component(261,99);
    final Component callButler = ctx.widgets.component(370,19).component(3);
    final Npc butler = ctx.npcs.select().id(BUTLER_ID).nearest().poll();
    final Component myFirstTradeItem = ctx.widgets.component(335, 25).component(0);
    final Component firstWindowAccept= ctx.widgets.component(335,12);
    final Component firstWindowDeclie = ctx.widgets.component(335,15);
    final Component secoundWindowAccept = ctx.widgets.component(334,25);
    final Component secoundWindowDecline = ctx.widgets.component(334,26);
    final Component hisFirstTradeItem = ctx.widgets.component(335, 28).component(0);
    int totalPlanks=0;
    public void start(){

    }


    @Override
    public void poll() {
        if(totalLogs<28||totalLumbTabs<20||totalCoins<100000||totalHouseTabs<20) {
            System.out.println("out of something");
            System.out.println(totalLogs+" "+totalLumbTabs+"  "+totalCoins+" "+totalHouseTabs);

        }

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
                    totalCoins = ctx.bank.select().id(COINS_ID).poll().stackSize();
                    totalLumbTabs = ctx.bank.select().id(LUMB_TAB_ID).poll().stackSize();
                    totalHouseTabs = ctx.bank.select().id(HOUSE_TAB_ID).poll().stackSize();
                    totalLogs = ctx.bank.select().id(LOGS_ID).poll().stackSize();
                    totalPlanks = ctx.bank.select().id(PLANKS_ID).poll().stackSize();
                    System.out.println("total coins,laws,planks:"+totalCoins+" "+totalLaws+" "+totalPlanks);
                    ctx.bank.withdrawModeNoted(false);
                    if(totalPlanks>=WANTED_PLANKS&&ctx.players.select().within(10).name(TRADE_WITH).size() == 1)
                        break;
                    System.out.println("deee:"+ctx.inventory.select().id(NOTED_PLANKS_ID).count(true)+" and"+ctx.inventory.select().id(PLANKS_ID).count(true)+" "+totalPlanks);
                    if(ctx.inventory.select().id(NOTED_PLANKS_ID).count(true)>0||ctx.inventory.select().id(PLANKS_ID).count(true)>0)
                        ctx.bank.depositInventory();
                    if (ctx.inventory.select().id(COINS_ID).poll().stackSize() < 80000)
                        ctx.bank.withdraw(COINS_ID, 400000);
                    if (ctx.inventory.select().id(HOUSE_TAB_ID).poll().stackSize() < 20)
                        ctx.bank.withdraw(HOUSE_TAB_ID, 100);
                    if (ctx.inventory.select().id(CAMMY_TAB_ID).poll().stackSize() < 20)
                        ctx.bank.withdraw(CAMMY_TAB_ID, 100);
                    if(ctx.bank.withdraw(LOGS_ID, 26))
                        totalPlanks +=26;

                    ctx.bank.close();
                }
                else{
                    ctx.movement.step(ctx.bank.nearest());
                    ctx.camera.turnTo(ctx.bank.nearest());
                }
                break;

            case TELEPORTHOUSE:
                System.out.println("state is:" +state);
                inv.click();
                houseTab = ctx.inventory.select().id(HOUSE_TAB_ID).poll();
                houseTab.interact("Break");
                Condition.sleep(Random.nextInt(700, 1500));
                break;

            case TELEPORTBANK:
                System.out.println("state is:" +state);
                if (!ctx.movement.running() && ctx.movement.energyLevel() > 20)
                    ctx.movement.running(true);

                inv.click();
                camTab = ctx.inventory.select().id(CAMMY_TAB_ID).poll();
                camTab.interact("Break");
                //lumbTab = ctx.inventory.select().id(LUMB_TAB_ID).poll();
                //lumbTab.interact("Break");
                Condition.sleep(Random.nextInt(700, 1500));
                break;

            case BUTLER:
                System.out.println("state is:" +state);
                if(!ctx.chat.chatting()) {
                    System.out.println("not chatting so im calling the butler");
                    Condition.sleep(Random.nextInt(700, 1500));
                    setting.click();
                    Condition.sleep(Random.nextInt(100, 200));
                    houseOptions.click();
                    callButler.click();
                }
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for butler");
                        if (!(ctx.chat.chatting() && ctx.npcs.select().within(10).name("Demon butler").size() == 1))
                        {setting.click();
                            Condition.sleep(Random.nextInt(50, 100));
                            houseOptions.click();
                            callButler.click();}
                        return ctx.chat.chatting() || ctx.npcs.select().within(10).name("Demon butler").size() == 1;
                    }
                }, Random.nextInt(1500, 2000), 7);

                if(!ctx.chat.chatting()) {
                    System.out.println("he is here but stuck");
                    ctx.npcs.select().name("Demon butler").poll().interact(false, "Talk-to");
                    Condition.sleep(Random.nextInt(200, 700));
                }
                while(ctx.chat.chatting()) {
                    if(!ctx.chat.canContinue())
                        ctx.input.sendln("" + 1);
                    else
                        ctx.chat.clickContinue(true);

                }

                break;

            case TRADE:
                System.out.println("state is:"+state);
                if(!ctx.bank.opened())
                    ctx.bank.open();
                Condition.sleep(Random.nextInt(500,1000));
                ctx.bank.depositInventory();
                ctx.bank.withdrawModeNoted(true);
                ctx.bank.withdraw(PLANKS_ID,WANTED_PLANKS);
                System.out.println("inv planks"+ctx.inventory.select().id(NOTED_PLANKS_ID).poll().stackSize());

                if(ctx.inventory.select().id(NOTED_PLANKS_ID).poll().stackSize()==-1)
                {
                    ctx.bank.depositInventory();
                    totalPlanks = ctx.bank.select().id(PLANKS_ID).poll().stackSize();
                    break;}

                ctx.bank.close();
                if(ctx.inventory.select().id(NOTED_PLANKS_ID).count(true)==0)
                    break;
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for trader to open trade window.");
                        if(attempts%10==0)
                            ctx.players.select().name(TRADE_WITH).poll().interact(false, "Trade with", TRADE_WITH);
                        attempts++;
                        return ctx.widgets.component(335,31).text().contains("Trading With");
                    }
                }, Random.nextInt(500, 1000), 30);
                attempts=0;
                if(!ctx.widgets.component(335,31).text().contains("Trading With"))
                    break;

                while(myFirstTradeItem.itemStackSize() == 0&&attempts<10) {
                    Condition.sleep(Random.nextInt(1000, 1200));
                    ctx.widgets.component(149,0).interact("Offer-All");
                    attempts++;

                }
                if(attempts>10)
                {
                    System.out.println("cant put the wanted amount of planks.");
                    break;
                }
                attempts=0;
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for him to accept.");

                        attempts++;
                        firstWindowAccept.click();
                        return ctx.widgets.component(334,30).text().contains("Trading with");
                    }
                },1000,22);
                if(attempts>20)
                {
                    System.out.println("he dosent accept! break.");
                    firstWindowDeclie.click();
                    break;
                }
                attempts=0;
                secoundWindowAccept.click();
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for him to accept2.");
                        attempts++;
                        return !ctx.widgets.component(334,30).text().contains("Trading with");
                    }
                },1000,12);
                if(attempts>10) {
                    System.out.println("second trade window dosent accept.");
                    secoundWindowDecline.click();
                    break;
                }
                attempts=0;
                System.out.println("Successful trade!");



        }
    }

    private State getState() {

        System.out.println("total planks,logs,laws,coins:"+totalPlanks+" "+totalLogs+" "+totalLaws+"  "+totalCoins);
        if(totalPlanks>=WANTED_PLANKS&&ctx.players.select().within(10).name(TRADE_WITH).size() == 1)//got wanted amount of planks, time to trade and trader is here
            return State.TRADE;
        else if(ctx.movement.distance(atCammy)<10&&ctx.movement.distance(atCammy)>=1&&ctx.inventory.select().id(LOGS_ID).count(true) ==0)//im at cammy and got no logs in inv
            return State.BANK;
        else if(ctx.movement.distance(atCammy)<10&&ctx.movement.distance(atCammy)>=1&&ctx.inventory.select().id(LOGS_ID).count(true) >0)//im at cammy and got logs
            return State.TELEPORTHOUSE;
        else if (ctx.movement.distance(atCammy)==-1&&ctx.inventory.select().id(LOGS_ID).count(true) >0)//im at house and got logs
            return State.BUTLER;
        else if(ctx.inventory.select().id(LOGS_ID).count(true) ==0&&ctx.movement.distance(atCammy)==-1)//no more logs and at house
            return State.TELEPORTBANK;

        return null;
    }
    private enum State{
        BANK,TELEPORTHOUSE,TELEPORTBANK,BUTLER,TRADE;
    }

}
