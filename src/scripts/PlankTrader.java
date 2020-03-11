package scripts;



import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

@Script.Manifest(
        name = "trader plank,jugs",
        description = "trading my bots",
        properties = "client = 4;"
)
public class PlankTrader extends PollingScript<ClientContext> {
    public int NOTED_LOGS_ID = 1522;//1552 oaks ,8836 mah

    final int NOTED_JUGS_WATER_ID = 1938;
    final int NOTED_JUGS_ID = 1936;
    public static final int LAW_RUNES_ID = 563;
    public static final int COINS_ID = 995;
    public int getTradeAmount,getItemId,money;
    Tile t = new Tile(3222,3217,0);
    java.util.Random randomNumber = new java.util.Random();
    final Component myFirstTradeItem = ctx.widgets.component(335, 25).component(0);
    final Component hisFirstTradeItem = ctx.widgets.component(335, 28).component(0);
    int n,i=0,offerI=0,trades=0,jugsTraded;
    Item notedLogs = ctx.inventory.select().id(NOTED_LOGS_ID).poll();
    Item lawRunes = ctx.inventory.select().id(LAW_RUNES_ID).poll();
    Item coins = ctx.inventory.select().id(COINS_ID).poll();
    Item jugs = ctx.inventory.select().id(NOTED_JUGS_ID).poll();
    final Component firstWindowAccept= ctx.widgets.component(335,12);
    final Component firstWindowDeclie = ctx.widgets.component(335,15);
    final Component secoundWindowAccept = ctx.widgets.component(334,25);
    final Component secoundWindowDecline = ctx.widgets.component(334,26);
    public void start(){

    }


    @Override
    public void poll() {
        if((notedLogs.stackSize() ==-1||lawRunes.stackSize()==-1||coins.stackSize()==-1)&&jugs.stackSize()==-1) {
            System.out.println("out of something! "+notedLogs+" "+lawRunes+" "+" coins");
            ctx.controller.stop();
        }
        final State state = getState();
        if(state ==null) {
            System.out.println("state is:" +state);
            return;
        }
        switch(state){

            case RELOCATION:
                System.out.println("state is:" +state);
                ctx.movement.step(t);
                Condition.sleep(Random.nextInt(1500, 2500));
                break;

            case TRADE:
                System.out.println("state is:" +state);
                ctx.widgets.component(162,58).component(0).click();
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        i++;
                        System.out.println("i is:"+i);
                        return i>3||ctx.widgets.component(335,31).text().contains("Trading With");
                    }
                }, 500, 10);
                i=0;


                if(!(ctx.widgets.component(335,31).text().contains("Trading With")))
                    break;

                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            System.out.println("waiting for his offer");
                            return ctx.widgets.component(335, 28).component(0).itemStackSize()>0||ctx.widgets.component(335, 28).component(0).itemStackSize()==-1;
                        }
                    }, 1000, 13);
                    if(ctx.widgets.component(335, 28).component(0).itemStackSize()==-1) {
                        System.out.println("stacksize is -1 so i cancel!");
                        break;
                    }

                    if(ctx.widgets.component(335, 28).component(0).itemStackSize()==0) {
                        System.out.println("havent recived anything in the trade, decline!");
                        ctx.widgets.component(335,15).click();
                        break;
                    }


                    getTradeAmount = ctx.widgets.component(335, 28).component(0).itemStackSize();
                    getItemId=hisFirstTradeItem.itemId();
                    //add here switch case
                switch (getItemId) {

                    case NOTED_JUGS_WATER_ID:

                        jugs = ctx.inventory.select().id(NOTED_JUGS_ID).poll();

                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                System.out.println("setting amount");
                                System.out.println("stacksize:"+ctx.widgets.component(335, 28).component(0).itemStackSize());
                                setAmount(jugs,getTradeAmount);
                                return ctx.widgets.component(335, 27).component(0).itemStackSize()>0||ctx.widgets.component(335, 27).component(0).itemStackSize()==-1;
                            }
                        }, 5000, 3);

                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                firstWindowAccept.click();
                                return !firstWindowAccept.text().equals("Accept");
                            }
                        }, 1000, 10);

                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                secoundWindowAccept.click();
                                return !secoundWindowAccept.text().equals("Accept");
                            }
                        }, 1000, 10);
                        break;


                    default:
                        money = (getItemId == 8783) ? 1550 : 300;
                        NOTED_LOGS_ID = (getItemId == 8783) ? 8836 : 1522;
                        notedLogs = ctx.inventory.select().id(NOTED_LOGS_ID).poll();
                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                System.out.println("setting amount");
                                setAmount(notedLogs, getTradeAmount);
                                return ctx.widgets.component(335, 28).component(0).itemStackSize()>0||ctx.widgets.component(335, 28).component(0).itemStackSize()==-1;
                            }
                        }, 5000, 3);
                        Condition.sleep(Random.nextInt(300, 500));
                        setAmount(lawRunes, getTradeAmount / 13);//offering law rune
                        Condition.sleep(Random.nextInt(300, 500));
                        setAmount(coins, getTradeAmount * money); //offer coins

                        if (checkLogs(getTradeAmount) && checkLaws(getTradeAmount) && checkCoins(getTradeAmount)) {
                            System.out.println("bad amount of something.");
                            System.out.println(checkLogs(getTradeAmount));
                            System.out.println(checkLaws(getTradeAmount));
                            System.out.println(checkCoins(getTradeAmount));
                            firstWindowDeclie.click();
                        }

                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            firstWindowAccept.click();
                            return !firstWindowAccept.text().equals("Accept");
                        }
                    }, 1000, 10);

                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            secoundWindowAccept.click();
                            return !secoundWindowAccept.text().equals("Accept");
                        }
                    }, 1000, 10);
                }
                break;

            case WAIT:
                System.out.println("state iS:" +state);
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        i++;
                        return i>30||ctx.widgets.component(162,58).component(0).text().contains("wishes to trade with you")||ctx.movement.distance(t) != 1;
                    }
                }, 1000, 30);
                i=0;
                ctx.movement.running(true);
                ctx.movement.running(false);
               /* n = randomNumber.nextInt(58-35)+35;
                while(n>=42 &&n<=50)
                    n = randomNumber.nextInt(58-35)+35;
                ctx.widgets.component(161,n).click();
                break;*/
                    //n = randomNumber.nextInt(7)+51;
                //ctx.widgets.component(161,n).click();
                break;

        }


    }
    public void setAmount(Item i,int amount){
        System.out.println("a");
        i.interact("Offer-X");
        Condition.wait(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return (ctx.widgets.component(162, 44).text().equals("Enter amount:"));
            }
        }, 1000, 10);
        if (!(ctx.widgets.component(162, 44).text().equals("Enter amount:"))) {
        System.out.println("offer - x broke");
        firstWindowDeclie.click();
        }

        ctx.input.sendln("" + amount);

    }

    public boolean checkLogs(int amount){
        int index;
        for(int i=0;i<27;i++)
            if(ctx.widgets.component(335,25).component(i).id()==NOTED_LOGS_ID)
                return ctx.widgets.component(335,25).component(i).itemStackSize()==amount;
        return false;
    }

    public boolean checkLaws(int amount){
        int index;
        for(int i=0;i<27;i++)
            if(ctx.widgets.component(335,25).component(i).id()==LAW_RUNES_ID)
                return ctx.widgets.component(335,25).component(i).itemStackSize()==amount/13;
        return false;
    }

    public boolean checkCoins(int amount){
        int index;
        for(int i=0;i<27;i++)
            if(ctx.widgets.component(335,25).component(i).id()==COINS_ID)
                return ctx.widgets.component(335,25).component(i).itemStackSize()==amount*300;
        return false;
    }

    private State getState() {
        System.out.println(notedLogs.stackSize() +" "+lawRunes.stackSize()+" "+coins.stackSize()+" "+jugs.stackSize());
        notedLogs = ctx.inventory.select().id(NOTED_LOGS_ID).poll();
        lawRunes = ctx.inventory.select().id(LAW_RUNES_ID).poll();
        coins = ctx.inventory.select().id(COINS_ID).poll();
        jugs = ctx.inventory.select().id(NOTED_JUGS_ID).poll();
        System.out.println("new");
        if ((notedLogs.stackSize() ==-1||lawRunes.stackSize()==-1||coins.stackSize()==-1)&&(jugs.stackSize()==-1))
            ctx.controller.stop();
        else if(ctx.movement.distance(t) != 1)
            return State.RELOCATION;
        else if(ctx.widgets.component(162,58).component(0).text().contains("wishes to trade with you"))
            return State.TRADE;
        else if (!(ctx.widgets.component(162,58).component(0).text().contains("wishes to trade with you")))
            return State.WAIT;


        return null;
    }
    private enum State{
        RELOCATION,WAIT,TRADE;
    }

}
