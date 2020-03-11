package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "ammys",
        description = "connection ammy with wool",
        properties = "client = 4;"
)
public class makeAmmy extends PollingScript<ClientContext> {
    Robot r = new Robot();
    final int AMULET_ID=21105;
    final int BALL_OF_WOOL_ID=1759;
    final int COMPLETE_AMULET_ID=21114;

    final Component craftAll = ctx.widgets.component(270,12,0);
    final Component selectItem = ctx.widgets.component(270,14,38);




    int totalAmmys =0;
    int craftedAmmys=0;
    int ammyLeft=-2;
    int ballOfWoolsLeft=-2;


    Item ammy;
    Item ballOfWool;

    java.util.Random randomNumber = new java.util.Random();

    public makeAmmy() throws AWTException {
    }


    public void start(){

    }


    @Override
    public void poll() {
        if(ammyLeft==0||ballOfWoolsLeft==0) {
            System.out.println("out of ammys or wools "+ammyLeft+" "+ballOfWoolsLeft);
            ctx.controller.stop();
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
                    craftedAmmys += ctx.inventory.select().id(COMPLETE_AMULET_ID).count();
                    ctx.bank.depositInventory();
                    ammyLeft=ctx.bank.select().id(AMULET_ID).poll().stackSize();
                    ballOfWoolsLeft=ctx.bank.select().id(BALL_OF_WOOL_ID).poll().stackSize();
                    totalAmmys = ctx.bank.select().id(COMPLETE_AMULET_ID).poll().stackSize();
                    ctx.bank.withdraw(AMULET_ID,14);
                    ctx.bank.withdraw(BALL_OF_WOOL_ID,14);
                    ctx.bank.close();

                }
                else{
                    ctx.movement.step(ctx.bank.nearest());
                    ctx.camera.turnTo(ctx.bank.nearest());
                }
                break;
            case CONNECT:
                System.out.println("state is:" +state);
                ammy = ctx.inventory.select().id(AMULET_ID).poll();
                ballOfWool = ctx.inventory.select().id(BALL_OF_WOOL_ID).poll();
                ammy.interact("Use");
                ballOfWool.interact("Use");

                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        if (ctx.chat.chatting()) {
                            System.out.println("inside the loop");
                            ctx.chat.clickContinue(true);
                            ammy = ctx.inventory.select().id(AMULET_ID).poll();
                            ballOfWool = ctx.inventory.select().id(BALL_OF_WOOL_ID).poll();
                            ammy.interact("Use");
                            ballOfWool.interact("Use");
                        }
                        if(ctx.widgets.component(270,0).valid())
                            selectItem.click();
                        return !(ctx.inventory.select().id(BALL_OF_WOOL_ID).count()>0&&ctx.inventory.select().id(AMULET_ID).count()>0);
                    }
                }, 2000, 15);

        }
    }

    private State getState() {
        System.out.println("ammy left,wools left,ammy crafted,total ammy " +ammyLeft +" " +ballOfWoolsLeft +" " +craftedAmmys +" " + totalAmmys+" ");
        if(ctx.inventory.select().id(BALL_OF_WOOL_ID).count()==0||ctx.inventory.select().id(AMULET_ID).count()==0)//no ammy/wool at inventory
            return State.BANK;
        else if(ctx.inventory.select().id(BALL_OF_WOOL_ID).count()>0&&ctx.inventory.select().id(AMULET_ID).count()>0)//still can craft
            return State.CONNECT;
        return null;
    }
    private enum State{
        BANK,CONNECT
    }

}
