package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;


import java.util.concurrent.Callable;

@Script.Manifest(
        name = "JugsTrader",
        description = "trading my bots",
        properties = "client = 4;"
)
public class NewTrader extends PollingScript<ClientContext> {
    public static final int NOTED_JUGS_ID = 1936;
   public int getTradeAmount;
   Tile t = new Tile(3222,3217,0);
    java.util.Random randomNumber = new java.util.Random();
    final Component myFirstTradeItem = ctx.widgets.component(335, 25).component(0);
    int n,i=0,offerI=0,trades=0,jugsTraded;
    Item Jug = ctx.inventory.select().id(NOTED_JUGS_ID).poll();
    public void start(){

    }


    @Override
    public void poll() {
        if(ctx.inventory.select().id(NOTED_JUGS_ID).count(true) <=0) {
            System.out.println("out of jugs!");
            ctx.widgets.component(161,38).click();
            ctx.widgets.component(182,12).click();
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
                if(ctx.widgets.component(335,31).text().contains("Trading With"))
                {
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
                        Jug.interact("Offer-X");
                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                return ctx.widgets.component(162, 44).text().equals("Enter amount:");
                            }
                        }, 500, 10);
                        if (!(ctx.widgets.component(162, 44).text().equals("Enter amount:"))) {
                            System.out.println("offer - x broke");
                            ctx.widgets.component(335, 15).click();
                            break;
                        }
                            ctx.input.sendln("" + getTradeAmount);
                            Condition.sleep(Random.nextInt(750, 1500));
                            while (ctx.widgets.component(335, 25).component(0).itemStackSize() != getTradeAmount&&offerI<10) {
                                if(myFirstTradeItem.itemStackSize()!=getTradeAmount)
                                    myFirstTradeItem.interact("Remove-All");

                                Condition.sleep(Random.nextInt(500, 1500));
                                System.out.println("i didnt offered anything trying to repeat.");
                                Jug.interact("Offer-X");
                                Condition.sleep(Random.nextInt(500, 1500));
                                getTradeAmount = ctx.widgets.component(335, 28).component(0).itemStackSize();
                                ctx.input.sendln("" + getTradeAmount);
                                offerI++;
                            }
                            if(offerI>=10)
                            {
                                System.out.println("offer x broke after loop decline");
                                System.out.println("offer - x broke");
                                ctx.widgets.component(335, 15).click();
                                break;
                            }
                            offerI=0;
                            ctx.widgets.component(335,12).click();
                            while(ctx.widgets.component(335,31).text().contains("Trading With")&&i<10)
                            {
                                ctx.widgets.component(335,12).click();
                                Condition.sleep(Random.nextInt(500, 1500));
                                i++;
                            }
                            if(i>=10)
                            {
                                System.out.println("why other player dosent accept? decline.");
                                ctx.widgets.component(335, 15).click();
                                break;
                            }
                            i=0;
                            while (ctx.widgets.component(334,30).text().contains("Trading with")&&i<10) {
                                System.out.println("waiting for secound trade accept");
                                ctx.widgets.component(334, 25).click();
                                Condition.sleep(Random.nextInt(500, 1500));
                                i++;
                            }
                    if(i>=10)
                    {
                        System.out.println("why other player dosent accept? decline.");
                        ctx.widgets.component(334, 26).click();
                        break;
                    }
                     jugsTraded+=getTradeAmount;
                    System.out.println("finished trades:"+(trades++ +1)+"jugs traded:"+jugsTraded);
                }


                break;
            case WAIT:
                System.out.println("state iS:" +state);
                final int index =Random.nextInt(50, 130);
                int time = Random.nextInt(500, 1000);
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        i++;
                        return i>index||ctx.widgets.component(162,58).component(0).text().contains("wishes to trade with you");
                    }
                }, time, index);
                i=0;
                n = randomNumber.nextInt(58-35)+35;
                while(n>=42 &&n<=50)
                    n = randomNumber.nextInt(58-35)+35;
                ctx.widgets.component(161,n).click();
                System.out.println("jugs:"+ctx.inventory.select().id(1936).poll().stackSize()+" jugs traded:"+jugsTraded+" total trades:"+trades);
                break;


        }


    }

    private State getState() {
//        if(ctx.movement.distance(t) != 1)
//            return State.RELOCATION;

//        for(Player p: ctx.players.select().within(50)){
//            System.out.println(p.appearance()[3]);
//            Condition.sleep(250);
//
//        }
         if(ctx.widgets.component(162,58).component(0).text().contains("wishes to trade with you"))
            return State.TRADE;
        else if (!(ctx.widgets.component(162,58).component(0).text().contains("wishes to trade with you")))
            return State.WAIT;


        return null;
    }
    private enum State{
        RELOCATION,WAIT,TRADE;
    }

}
