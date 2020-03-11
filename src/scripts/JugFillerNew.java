package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;


import java.util.concurrent.Callable;

@Script.Manifest(
        name = "aFilling jugs",
        description = "filling jugs of water in PVP worlds f2p",
        properties = "client = 4;"
)
public class JugFillerNew extends PollingScript<ClientContext> {
    public static final int JUGS_ID=1935;
    public static final int JUGS_NOTED_ID=1936;
    public static final int JUGS_OF_WATER_ID=1937;
    public static final int JUGS_OF_WATER_NOTED_ID=1938;
    public static final int WANTEDJUGS =1000;
    public static final int FOUNTAIN_ID = 879;
    public int totalJugsFilled =0;

    int attempts =0;
    public static int numberOfTrades=0;
    public int jugsInBank=20;
    public static final String TRADE_WITH = "nubpker33";
    final Tile tile = new Tile(3222, 3217, 0);
    final Component firstWindowAccept= ctx.widgets.component(335,12);
    final Component firstWindowDeclie = ctx.widgets.component(335,15);
    final Component secoundWindowAccept = ctx.widgets.component(334,25);
    final Component secoundWindowDecline = ctx.widgets.component(334,26);
    Item notedJugs = ctx.inventory.select().id(JUGS_OF_WATER_NOTED_ID).poll();

    public void start(){
        if(ctx.bank.open()) {
            ctx.bank.depositInventory();
            if(ctx.bank.select().id(JUGS_ID).count()==-1)
                ctx.controller.stop();
        }
    }


    @Override
    public void poll() {
        // (empty jugs)
       /* if(jugsInBank <=0)
            ctx.controller.stop();*/
        final State state = getState();
    if(state ==null) {
     System.out.println("state is:" +state);
        return;
    }
    switch(state){
        case BANK:
            System.out.println("state is:" +state);
            Condition.sleep(Random.nextInt(500, 1000));
            totalJugsFilled += ctx.inventory.select().id(JUGS_OF_WATER_ID).count();
                ctx.bank.depositInventory();
                System.out.println("total jugs filled:"+totalJugsFilled);
                  jugsInBank =ctx.bank.select().id(JUGS_OF_WATER_ID).poll().stackSize();//System.out.println(ctx.inventory.select().id(JUGS_ID).count());
                if(jugsInBank>WANTEDJUGS &&traderHere())
                    break;
                  ctx.bank.withdraw(JUGS_ID,28);
                System.out.println("total jugs in bank:"+ jugsInBank);
                ctx.bank.close();

            break;
        case TRADE:
            System.out.println("state is:" +state);
            if (ctx.bank.open()) {
                Condition.sleep(Random.nextInt(1500, 3000));
                ctx.bank.depositInventory();
                ctx.bank.withdrawModeNoted(true);
                ctx.bank.withdraw(JUGS_OF_WATER_ID, WANTEDJUGS);
                ctx.bank.close();
                if(ctx.inventory.select().id(JUGS_OF_WATER_NOTED_ID).count(true)==0)
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

                notedJugs = ctx.inventory.select().id(JUGS_OF_WATER_NOTED_ID).poll();
                while(notedJugs.stackSize() != -1&&attempts<15) {
                    notedJugs = ctx.inventory.select().id(JUGS_OF_WATER_NOTED_ID).poll();
                    System.out.println("offer all loop");
                    System.out.println(notedJugs.stackSize());
                    Condition.sleep(Random.nextInt(1000, 1200));
                    notedJugs.interact("Offer-All");
                    attempts++;

                }
                if(attempts>15)
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
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for him to accept2.");
                        secoundWindowAccept.click();
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
                jugsInBank-=WANTEDJUGS;
            }

            else {
                ctx.movement.step(ctx.bank.nearest());
                ctx.camera.turnTo(ctx.bank.nearest());
            }
            break;

        case WALK_TO_BANK:
            System.out.println("state issss:" +state);
            if(ctx.bank.inViewport()) {
                //Condition.sleep(Random.nextInt(1000, 2000));
                if (ctx.bank.open()) {
                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            return ctx.bank.opened();
                        }
                    }, 500, 100);
                }
            }
            else {
                ctx.movement.step(ctx.bank.nearest());
                ctx.camera.turnTo(ctx.bank.nearest());
            }

            break;
        case WALK_TO_FOUNTAIN:
            System.out.println("state is:" +state);
            if (!ctx.movement.running() && ctx.movement.energyLevel() > 20)
                ctx.movement.running(true);
            System.out.println(ctx.inventory.select().id(JUGS_ID).count());
            GameObject Fountain = ctx.objects.select().id(FOUNTAIN_ID).poll();
            Item Jug = ctx.inventory.select().id(JUGS_ID).poll();
            if(Fountain.inViewport()) {
                Condition.sleep(Random.nextInt(1000, 2000));
                Jug.interact("Use");
                Fountain.interact(false,"Use");
            }
            else
            {
                ctx.movement.step(Fountain);
                ctx.camera.turnTo(Fountain);
            }
        break;


    }


    }

    private State getState() {
     /*   System.out.println("trade state:" + JugsInBank+" " + totalJugsFilled+" "+WANTEDJUGS+" "+ctx.players.select().within(10).name(TRADE_WITH).size()+" "+ctx.bank.opened());
        System.out.println("walk to fountain state:" + ctx.inventory.select().id(JUGS_ID).count()+" "+ctx.players.local().animation()+" ");
        System.out.println("walk to bank state:" + ctx.inventory.select().id(JUGS_ID).count()+" "+!(ctx.bank.opened()));
        System.out.println("bank state:" +ctx.bank.opened());
        */
     for(int i=0;i<28;i++)
        System.out.println(ctx.widgets.component(149,0).itemIds()[i]);
        if(jugsInBank>=WANTEDJUGS&&traderHere()&&ctx.bank.opened())
            return State.TRADE;
        else if (ctx.inventory.select().id(JUGS_ID).count()>1 &&ctx.players.local().animation() == -1)
            return State.WALK_TO_FOUNTAIN;
        else if (ctx.inventory.select().id(JUGS_ID).count()==0&&!(ctx.bank.opened()))
            return State.WALK_TO_BANK;
        else if (ctx.bank.opened())
            return State.BANK;

        return null;
    }
    private enum State{
        WALK_TO_FOUNTAIN,WALK_TO_BANK,TRADE,BANK;
    }
    public boolean traderHere(){return ctx.players.select().within(10).name(TRADE_WITH).size() == 1;}
}
