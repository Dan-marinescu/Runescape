package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "Dicer",
        description = "dice",
        properties = "client = 4;"
)
public class Doubling extends PollingScript<ClientContext> {
    public static final int COINS_ID = 995;
    public int getTradeAmount;
    Tile t = new Tile(3201,3467,2);
    java.util.Random randomNumber = new java.util.Random();
    final Component myFirstTradeItem = ctx.widgets.component(335, 25).component(0);
    final Component firstWindowAccept= ctx.widgets.component(335,12);
    final Component firstWindowDeclie = ctx.widgets.component(335,15);
    final Component secoundWindowAccept = ctx.widgets.component(334,25);
    final Component secoundWindowDecline = ctx.widgets.component(334,26);
    int diceRolled,i=0,offerI=0,trades=0,jugsTraded;
    int playerBet;
    Item Jug = ctx.inventory.select().id(0).poll();
    Queue<String> users = new LinkedList<String>();
    String Player;

    public void start(){

    }


    public String getTraderName(){
        String myString = ctx.widgets.component(335,31).text();
        myString =myString.substring(14);
        return myString;
    }

    public int dice(int bet){
        return 80;
    }

    @Override
    public void poll() {
        if(ctx.inventory.select().id(COINS_ID).count(true) <=0) {
            System.out.println("out of money!");
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
                if(ctx.widgets.component(335,31).text().contains("Trading With")) {
                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            System.out.println("waiting for his offer-----------");
                            return (ctx.widgets.component(335, 28).component(0).itemStackSize() > 0 && ctx.widgets.component(335, 28).component(0).itemId() == COINS_ID) || ctx.widgets.component(335, 28).component(0).itemStackSize() == -1;
                        }
                    }, 1000, 20);
                    if (ctx.widgets.component(335, 28).component(0).itemStackSize() == -1) {
                        System.out.println("other has canceled trade");
                        break;
                    }

                    if (ctx.widgets.component(335, 28).component(0).itemStackSize() == 0) {
                        ctx.input.sendln("red:nothing has been offered!");
                        firstWindowDeclie.click();
                        break;
                    }
                }
                    Player=getTraderName();
                    playerBet=ctx.widgets.component(335,28).component(0).itemStackSize();
                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            System.out.println("waiting for his accept");
                            firstWindowAccept.click();
                            System.out.println(ctx.widgets.component(334,30).text());
                            return ctx.widgets.component(334,30).text().contains("Trading With:<br>"+Player);
                        }
                    }, 1000, 10);


                    System.out.println(ctx.widgets.component(334,29).component(0).text());
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for his accept2");
                        System.out.println(ctx.widgets.component(334,29).component(0).text());
                        secoundWindowAccept.click();
                        return !ctx.widgets.component(334,4).text().contains("Are you");
                    }
                }, 1000, 10);

                    diceRolled=dice(playerBet);
                    System.out.println("dice rolled:"+diceRolled);
                    if(diceRolled>55)
                    {
                        ctx.input.sendln("green:"+Player+" has won "+playerBet*2+"!!" );
                        if(ctx.players.select().within(3).name(Player).poll().interact(false, "Trade with", Player))
                        {
                            ctx.input.sendln(""+playerBet*2);
                            while(myFirstTradeItem.itemStackSize()!=playerBet*2){
                                if(myFirstTradeItem.itemStackSize()!=0)
                                {
                                    myFirstTradeItem.interact("Remove-All");
                                    Jug.interact("Offer-X");
                                    Condition.sleep(Random.nextInt(500, 1500));
                                    ctx.input.sendln(""+playerBet*2);
                                }
                                else
                                {
                                    Jug.interact("Offer-X");
                                    Condition.sleep(Random.nextInt(500, 1500));
                                    ctx.input.sendln(""+playerBet*2);
                                }

                            }
                            System.out.println("finished while loop.");
                            firstWindowAccept.click();
                            Condition.wait(new Callable<Boolean>() {
                                public Boolean call() throws Exception {
                                    System.out.println("waiting for his accept");
                                    firstWindowAccept.click();
                                    return ctx.widgets.component(334,4).text().contains("Are you");
                                }
                            }, 1000, 10);
                            secoundWindowAccept.click();
                            Condition.wait(new Callable<Boolean>() {
                                public Boolean call() throws Exception {
                                    System.out.println("waiting for his accept");
                                    secoundWindowAccept.click();
                                    return !ctx.widgets.component(334,4).text().contains("Are you");
                                }
                            }, 1000, 10);
                            ctx.input.sendln("green:thank you!");
                        }
                        else
                            ctx.input.sendln("red:"+Player+" is too far!" );

                    }



                    /*Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {

                    getTradeAmount = ctx.widgets.component(335, 28).component(0).itemStackSize();
                    Jug.interact("Offer-X");
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
                        if(myFirstTradeItem.itemStackSize()>0)
                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {e()!=getTradeAmount)
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

*/
                break;
            case WAIT:
                System.out.println("state iS:" +state);
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        i++;
                        return i>30||ctx.widgets.component(162,58).component(0).text().contains("wishes to trade with you");
                    }
                }, 1000, 30);
                i=0;

                ctx.input.sendln("white:hi" );
                break;


        }


    }

    private State getState() {
        if(ctx.movement.distance(t) != 1)
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
