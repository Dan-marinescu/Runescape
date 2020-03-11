package scripts;

import com.sun.javafx.image.BytePixelSetter;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "AFlecher",
        description = "fleching by Kaskas",
        properties = "client = 4;"
)
public class Fleching extends PollingScript<ClientContext> implements PaintListener{
    int LOGS;
    int prev;
    int count = 0;
    int min=27;
    int x300 =300;
    int startXp;
    final int KNIFE =946;
    final int NORMAL_LOGS= 1511;
    final int OAK_LOGS= 1521;
    final int WILLOW_LOGS= 1519;
    final int MAPLE_LOGS= 1517;
    final int YEW_LOGS= 1515;
    final int MAGIC_LOGS= 1513;

    boolean shortbowFlag = false;
    boolean longBowFlag = false;
    boolean arrowsFlag = false;

    final Component shaftsComp = ctx.widgets.component(270,14,29);
    final Component shortbowComp = ctx.widgets.component(270,15,29);
    final Component longbowComp = ctx.widgets.component(270,16,29);
    final Component normalLongbowComp =ctx.widgets.component(270,17,29);
    final Component inventoryIcon = ctx.widgets.component(161,54);

    //Item knife = ctx.inventory.select().id(KNIFE).poll();

    java.util.Random randomNumber = new java.util.Random();

    public void start(){
        startXp = ctx.skills.experience(Constants.SKILLS_FLETCHING);
        String typeOfItem[] = {"Arrows","Shortbow","Longbow"};
        String userItemTypeChoice =""+JOptionPane.showInputDialog(null,"what type of item?","Fleching", JOptionPane.PLAIN_MESSAGE,null,typeOfItem,typeOfItem[0]);
        String typeOfLogs[] ={"Normal","Oak","Willow","Maple","Yew","Magic"};
        String useLogTypeChoice =""+JOptionPane.showInputDialog(null,"what type of logs?","Fleching", JOptionPane.PLAIN_MESSAGE,null,typeOfLogs,typeOfLogs[0]);


        if(useLogTypeChoice.equals("Oak"))
            LOGS=OAK_LOGS;
        else if(useLogTypeChoice.equals("Willow"))
            LOGS = WILLOW_LOGS;
        else if(useLogTypeChoice.equals("Maple"))
            LOGS = MAPLE_LOGS;
        else if(useLogTypeChoice.equals("Yew"))
            LOGS = YEW_LOGS;
        else if(useLogTypeChoice.equals("Magic"))
            LOGS = MAGIC_LOGS;
        else
            LOGS = NORMAL_LOGS;

        if(userItemTypeChoice.equals("Arrows"))
            arrowsFlag=true;
        else if(userItemTypeChoice.equals("Shortbow"))
            shortbowFlag=true;
        else
            longBowFlag=true;
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
                        }, 500, 10);
                    }
                    ctx.bank.depositAllExcept(KNIFE);
                    if(ctx.bank.select().id(LOGS).count(true)==0&&ctx.bank.opened()){
                        System.out.println("Out of logs.");
                        ctx.controller.stop();
                    }
                    ctx.bank.withdraw(LOGS,27);
                    if(ctx.inventory.select().id(KNIFE).count()==0)
                        ctx.bank.withdraw(KNIFE,1);
                    ctx.bank.close();
                }
                else{
                    ctx.movement.step(ctx.bank.nearest());
                    ctx.camera.turnTo(ctx.bank.nearest());
                }
                break;

            case FLECH:
                System.out.println("state is:" +state);
                min=27;
                Item log = ctx.inventory.select().id(LOGS).poll();
                Item knife = ctx.inventory.select().id(KNIFE).poll();
                knife.interact("Use");
                log.interact("Use");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.widgets.component(270,5).valid();
                    }
                },500,5);
                if(!ctx.widgets.component(270,5).valid())
                    break;
                if(arrowsFlag)
                    shaftsComp.click();
                else if(shortbowFlag) {
                    if(LOGS==NORMAL_LOGS)
                        longbowComp.click();
                    else
                        shortbowComp.click();
                }
                else if(longBowFlag){
                    if(LOGS==NORMAL_LOGS)
                        normalLongbowComp.click();
                    else
                        longbowComp.click();
                }
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() != -1;
                    }
                },500,10);
                break;

            case ACTIVE:
                System.out.println("state is:" +state);
                if(ctx.inventory.select().id(LOGS).count()<min){
                    min=ctx.inventory.select().id(LOGS).count();
                    count++;
                    if(min==1)
                        count++;
                }
                break;
        }
    }

    private State getState() {
        if(count>x300){
            x300+= org.powerbot.script.Random.nextInt(275, 340);
            System.out.println("reached milestone:"+count);
            ctx.worlds.select().types(World.Type.MEMBERS).joinable().shuffle().peek().hop();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.client().getClientState()==30;
                }
            },500,10);
            inventoryIcon.click();
        }
        if(ctx.players.local().animation() != -1)
            return State.ACTIVE;
        else if(ctx.players.local().animation() == -1&&ctx.inventory.select().id(LOGS).count()==0)
            return State.BANK;
        else if(ctx.players.local().animation() == -1&&ctx.inventory.select().id(LOGS).count()!=0)
            return State.FLECH;
        return null;
    }
    private enum State{
        BANK,FLECH,ACTIVE
    }

    @Override
    public void repaint(Graphics graphics){
        long milliseconds =this.getTotalRuntime();
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60) % 60);
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        int xpGained =ctx.skills.experience(Constants.SKILLS_FLETCHING)-startXp;
        Graphics2D g =(Graphics2D)graphics;
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0,0,200,150);

        g.setColor(new Color(255,255,255));
        g.drawRect(0,0,200,150);

        g.drawString("Flech by Kaskas",20,20);
        g.drawString("running: "+String.format("%02d:%02d:%02d",hours,minutes,seconds),20,40);
        g.drawString("logs P/H:"+ (int)(count*(3600000D/milliseconds)),20,60);
        g.drawString("logs fleched:"+ count,20,80);
        g.drawString("xp P/H:"+String.format("%.2f",xpGained*(3600000D/milliseconds)),20,100);

    }

}

