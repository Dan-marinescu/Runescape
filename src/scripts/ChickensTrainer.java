package scripts;

import org.powerbot.Con;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import javax.swing.*;

@Script.Manifest(
        name = "Chickens trainer",
        description = "Kill, loot feathers, bury bones, cook chickens",
        properties = "client = 4;"
)

public class ChickensTrainer extends PollingScript<ClientContext>  implements PaintListener{
    Random random = new Random();
    String monsterNameInput;

    boolean buryFlag = false;
    boolean feathersFlag = false;

    final boolean [] randomBoolean = {true,false};

    final int BONES_ID = 526;
    final int FEATHER_ID = 314;

    int [] lootID = {995,884,882,439, 437};
    int [] junkIDs = {2140,2144,1944,23182,2138,1155,1205};

    int randomRun = 5 ;
    int randomNum =5;
    int startAttack;
    int startStr;
    int startDef;


    GroundItem loot =ctx.groundItems.select().id(lootID).nearest().poll();

    Npc monster;

    GameObject Door = ctx.objects.select().id(1535).nearest().poll();


    public void start(){
        monsterNameInput = JOptionPane.showInputDialog("enter monster name (key sensitive):");
        monster = ctx.npcs.select().name(monsterNameInput).nearest().poll();
        buryFlag = (JOptionPane.showInputDialog("bury bones? (Y/N)").equals("Y") ? true : false);
        feathersFlag = (JOptionPane.showInputDialog("collect feathers? (Y/N)").equals("Y") ? true : false);
        if(feathersFlag)
            lootID = extendArray(lootID.length,lootID,FEATHER_ID);
        if(buryFlag)
            lootID = extendArray(lootID.length,lootID,BONES_ID);
        else
            junkIDs = extendArray(junkIDs.length,junkIDs,BONES_ID);
        startAttack = ctx.skills.experience(Constants.SKILLS_ATTACK);
        startStr = ctx.skills.experience(Constants.SKILLS_STRENGTH);
        startDef = ctx.skills.experience(Constants.SKILLS_DEFENSE);
    }



    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {
            System.out.println("state is:" + state);
            return;
        }
        switch(state) {

            case FIGHT:
                System.out.println("state is:" + state);
                if(!monster.inViewport()){
                    ctx.movement.step(monster);
                    ctx.camera.turnTo(monster);
                }

                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        if(monster.healthPercent() != 0)
                            monster.interact(randomBoolean[random.nextInt(1)],"Attack");
                        System.out.println(monster.healthPercent());
                        return monster.healthBarVisible() || monster.healthPercent() == 0;
                    }
                }, 1000, 5);

                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("fighting");
                        return monster.healthPercent()==0;
                    }
                }, 300, 30);
                break;

            case LOOT:
                System.out.println("state is:" + state);
                if(!loot.inViewport())
                    ctx.camera.turnTo(loot);

                if(ctx.inventory.isFull() || !loot.tile().matrix(ctx).reachable())
                    break;
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        loot.interact(false,"Take");
                        loot =ctx.groundItems.select().id(lootID).nearest().poll();
                        return (!loot.valid()||ctx.inventory.isFull())&&inRange(loot.tile()) ||!loot.tile().matrix(ctx).reachable();
                    }
                }, 1500, 10);
                break;

        }
    }

    private State getState() {
        health();
        refresh();
        if (!ctx.movement.running() && ctx.movement.energyLevel() > randomRun) {
            ctx.movement.running(true);
            randomRun = random.nextInt(5)+8;
        }

        if((ctx.inventory.select().id(junkIDs).count()+ctx.inventory.select().id(BONES_ID).count() > randomNum)||(ctx.inventory.isFull() && ctx.inventory.select().id(BONES_ID).count()>0)) {
            randomNum = random.nextInt(2)+1;
            refreshInventory();
        }
        if(loot.valid() && !ctx.players.local().interacting().valid() && loot.tile().matrix(ctx).reachable() && !ctx.inventory.isFull())
            return State.LOOT;
        else if(monster.valid() && shouldAttack() && monster.tile().matrix(ctx).reachable())
            return State.FIGHT;
        else
            return null;
    }

    private enum State{
        LOOT,FIGHT,COOK
    }

    public void refresh(){
        loot = ctx.groundItems.select().id(lootID).nearest().poll();
        if(ctx.combat.inMultiCombat()){
        monster = ctx.npcs.select().name(monsterNameInput).nearest().poll();
        }else {
            monster = ctx.npcs.select().name(monsterNameInput).select(new Filter<Npc>(){
                @Override
                public boolean accept(Npc npc){
                    return !monster.healthBarVisible() && !monster.interacting().valid();
                }
                @Override
                public boolean test(Npc npc){return true;}
            }).nearest().poll();
        }

    }
    public void health() {
        if (ctx.combat.healthPercent() < 60)
            if (ctx.inventory.select().name("Salmon").count() != 0)
                ctx.inventory.select().name("Salmon").poll().interact("Eat");
            else
                ctx.controller.stop();
    }
    public void refreshInventory(){
        while(ctx.inventory.select().id(BONES_ID).count()>0){
        for(Item t:ctx.inventory.select().id(BONES_ID)){
            final int startingBones = ctx.inventory.select().id(BONES_ID).count();
            t.interact(randomBoolean[random.nextInt(1)],"Bury");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().id(BONES_ID).count()!= startingBones;
                }
            },55,20);
        }
        }

        for(Item t:ctx.inventory.select().id(junkIDs)){
            final int startingJunk= ctx.inventory.select().id(junkIDs).count();
            if(ctx.inventory.shiftDroppingEnabled())
                ctx.inventory.drop(t,true);
            else
                ctx.inventory.drop(t,false);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().id(junkIDs).count()!= startingJunk;
                }
            },25,20);
        }
    }

    public boolean shouldAttack(){
        return !monster.healthBarVisible() && !ctx.players.local().interacting().valid() && monster.tile().matrix(ctx).reachable();
    }

    public int [] extendArray(int size,int [] arr,int item){
        int [] newArr = new int[size+1];
        for (int i =0;i<size;i++)
            newArr[i] = arr[i];
        newArr[size] = item;
        return newArr;
    }

    public boolean atArea(Tile t){
        return (ctx.movement.distance(t)>=1 && ctx.movement.distance(t)<=4);
    }

    public boolean atHouse(Tile t){
        return  (t.x()<=3230 && t.y()<=3294);
    }


    //check if loot in the chicken area
    public boolean inRange(Tile t){
        return ctx.players.local().tile().distanceTo(t)<15 && t.matrix(ctx).reachable();
    }

    @Override
    public void repaint(Graphics graphics){
        long milliseconds =this.getTotalRuntime();
        long seconds = (milliseconds / 1000);
        long minutes = (milliseconds / (1000 * 60) % 60);
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        Graphics2D g =(Graphics2D)graphics;
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0,0,240,160);

        g.setColor(new Color(255,255,255));
        g.drawRect(0,0,240,160);
        g.drawString("attack exp gained:"+ (ctx.skills.experience(Constants.SKILLS_ATTACK)-startAttack),20,20);
        g.drawString("str exp gained:"+ (ctx.skills.experience(Constants.SKILLS_STRENGTH)-startStr),20,40);
        g.drawString("def exp gained:"+ (ctx.skills.experience(Constants.SKILLS_DEFENSE)-startDef),20,60);

        g.drawString("state is:" +getState(),20,100);


    }


}
