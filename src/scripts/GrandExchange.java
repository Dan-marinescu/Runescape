package scripts;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.*;
import java.util.concurrent.Callable;

public class GrandExchange {
    private ClientContext ctx;
    private final int COINS_ID = 995;
   // private final Npc geWorker = ctx.npcs.select().name("Grand Exchange Clerk").nearest().poll();
   Component amountComp;
    Component raisePriceComp ;
    Component reducePriceComp ;
    Component confirmComp;
    Component collectComp;
    Component setPriceComp;


    public GrandExchange(ClientContext ctx){
        this.ctx = ctx;
         amountComp = ctx.widgets.component(465,24,49);
         raisePriceComp = ctx.widgets.component(465,24,53);
         reducePriceComp = ctx.widgets.component(465,24,50);
         confirmComp = ctx.widgets.component(465,24,54);
         collectComp = ctx.widgets.component(465,6,1);
         setPriceComp = ctx.widgets.component(465,24,12);


    }


    public boolean buyItemByPrice(String name,int amount,int price){
        if(ctx.bank.inViewport()) {
            if (ctx.bank.open()) {
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for bank open2");
                        ctx.bank.open();
                        return ctx.bank.opened();
                    }
                }, 3000, 5);

            }
            if(ctx.inventory.isFull())
                ctx.bank.depositInventory();
            ctx.bank.withdraw(COINS_ID, ctx.bank.select().id(COINS_ID).count(true));
            ctx.bank.close();
            ctx.npcs.select().name("Grand Exchange Clerk").poll().interact(false, "Exchange");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(465, 2, 1).valid();
                }
            }, 250, 8);
            int i;
            for (i = 7; i <= 14; i++)
                if (ctx.widgets.component(465, i, 0).visible())
                    break;

            final int k = i;
            System.out.println("empty box is:" + i);
            ctx.widgets.component(465, i, 0).click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(465, 24).visible();
                }
            }, 1000, 3);
            if(!ctx.widgets.component(465,24).visible())
                return false;
            ctx.input.send("" + name);
            for (Component c : ctx.widgets.component(162, 53).components()) {
                System.out.println(c.text());

                if (name.contains(c.text())) {
                    c.click();
                }
            }
            Condition.sleep(150);
            amountComp.click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(162, 44).visible();
                }
            }, 1000, 3);
            if(!ctx.widgets.component(162,44).visible())
                return false;
            ctx.input.sendln("" + amount);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ! ctx.widgets.component(162, 44).visible();
                }
            },350,8);

            setPriceComp.click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(162,44).visible();
                }
            },100,20);
            if(!ctx.widgets.component(162,44).visible())
                return false;
            ctx.input.sendln(""+price);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.widgets.component(162,44).visible();
                }
            },100,20);
            confirmComp.click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(465,k,22).textColor()==24320;
                }
            },1000,120);
            if(collectComp.visible())
                collectComp.click();
            return true;

        }
        else{
            return false;
        }
    }
    public void buyItem(String name,int amount,String raiseOrReduce,int raiseOrReduceTimes){
        if(ctx.bank.inViewport()) {
            if (ctx.bank.open()) {
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for bank open2");
                        ctx.bank.open();
                        return ctx.bank.opened();
                    }
                }, 3000, 5);

            }
            ctx.bank.depositInventory();
            ctx.bank.withdraw(COINS_ID,10000000);
            ctx.bank.close();
            ctx.npcs.select().name("Grand Exchange Clerk").poll().interact(false,"Exchange");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(465,2,1).valid();
                }
            },250,8);
            int i;
            for(i=7;i<=14;i++)
                if(ctx.widgets.component(465,i,0).visible())
                    break;

            final int k = i;
            System.out.println("empty box is:"+ i);
            ctx.widgets.component(465,i,0).click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(465,24).visible();
                }
            },1000,3);
            ctx.input.send(""+name);
            for(Component c: ctx.widgets.component(162,53).components()) {
                System.out.println(c.text());

                if (name.contains(c.text())) {
                    c.click();
                }
            }
            Condition.sleep(150);
            amountComp.click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(162,44).visible();
                }
            },1000,3);
            ctx.input.sendln(""+amount);
            for(int j=0;j<raiseOrReduceTimes;j++) {
                if (raiseOrReduce.equals("raise"))
                    raisePriceComp.click();
                else
                    reducePriceComp.click();
            }
            confirmComp.click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(465,k,22).textColor()==24320;
                }
            },3000,30);
            if(ctx.widgets.component(465,6,1).visible()){
                collectComp.click();
            }else{
                System.out.println("cant buy!");
            }

        }else{
            ctx.movement.step(ctx.bank.nearest());
            ctx.camera.turnTo(ctx.bank.nearest());
            ctx.bank.open();
        }
    }

    public boolean sellItem(final int wantedId, int price){
        ctx.npcs.select().name("Grand Exchange Clerk").poll().interact(false,"Exchange");
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.widgets.component(465,2,1).valid();
            }
        },250,8);
        int i;
        for(i=7;i<=14;i++)
            if (ctx.widgets.component(465, i, 0).visible())
                break;

        if(i==15) {
            System.out.println("no available slot");
            return false;
        }
        final int k = i;
        ctx.inventory.select().id(wantedId+1).poll().interact("Offer");
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select().id(wantedId+1).count(true)==0;
            }
        },50,100);
        setPriceComp.click();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.widgets.component(162,44).visible();
            }
        },100,20);
        ctx.input.sendln(""+price);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !ctx.widgets.component(162,44).visible();
            }
        },100,20);
        confirmComp.click();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.widgets.component(465,k,22).textColor()==24320;
            }
        },1000,120);
        if(collectComp.visible())
            collectComp.click();
        return true;



    }


}
