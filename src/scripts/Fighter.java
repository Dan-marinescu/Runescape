package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;


import javax.swing.*;
import javax.swing.plaf.synth.SynthStyle;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "aFighter",
        description = "mine",
        properties = "client = 4;"
)
public class Fighter extends PollingScript<ClientContext> {
    int worldsHopped =0;
    int kills =0;
    int x5=0;
    int hp = ctx.skills.level(Constants.SKILLS_HITPOINTS);
    final int EMPTY_VIAL_ID = 229;
    final int RESTORE_4_ID = 3024;
    final int RESTORE_3_ID = 3026;
    final int RESTORE_2_ID = 3028;
    final int RESTORE_1_ID = 3030;
    final int ATTACK_4_ID = 2436;
    final int ATTACK_3_ID = 145;
    final int ATTACK_2_ID = 147;
    final int ATTACK_1_ID = 149;
    final int STR_4_ID = 2440;
    final int STR_3_ID = 157;
    final int STR_2_ID = 159;
    final int STR_1_ID = 161;
    final int DWARVEN_ROCK_ID = 7510;
    final int [] monstersID ={2179,2170,2178,2177,2171};
    final int [] lootID={6529,2134,6523,6525,6568,10636,13443,6522,23652,6528,6524,21298,21301,21304};
    int i=0,n=0;
    boolean hopFlag =false;
    boolean bankFlag = false;
    boolean prayerFlag = true;
    String input;
    java.util.Random randomNumber = new java.util.Random();
    Item emptyVial = ctx.inventory.select().id(EMPTY_VIAL_ID).poll();
    Item restore4 = ctx.inventory.select().id(RESTORE_4_ID).poll();
    Item restore3 = ctx.inventory.select().id(RESTORE_3_ID).poll();
    Item restore2 = ctx.inventory.select().id(RESTORE_2_ID).poll();
    Item restore1 = ctx.inventory.select().id(RESTORE_1_ID).poll();
    Item str4 = ctx.inventory.select().id(STR_4_ID).poll();
    Item str3 = ctx.inventory.select().id(STR_3_ID).poll();
    Item str2 = ctx.inventory.select().id(STR_2_ID).poll();
    Item str1 = ctx.inventory.select().id(STR_1_ID).poll();
    Item attack4 = ctx.inventory.select().id(ATTACK_4_ID).poll();
    Item attack3 = ctx.inventory.select().id(ATTACK_3_ID).poll();
    Item attack2 = ctx.inventory.select().id(ATTACK_2_ID).poll();
    Item attack1 = ctx.inventory.select().id(ATTACK_1_ID).poll();
    Item dwarvenRock = ctx.inventory.select().id(DWARVEN_ROCK_ID).poll();
    GroundItem loot =ctx.groundItems.select().id(lootID).nearest().poll();
    Component inv = ctx.widgets.component(161,54);
    Component prayerPoints = ctx.widgets.component(160,15);
    Component prayerIcon = ctx.widgets.component(161,56);
    Component activatePrayer = ctx.widgets.component(541,19,0);
    Npc monster = ctx.npcs.select().id(monstersID).nearest().poll();
    Tile atLumb = new Tile(3221,3219,0);

    public void start(){
        input = JOptionPane.showInputDialog("enter item name:");

    }

    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {

            return;
        }

        switch(state){
            case DRINK:
                System.out.println("state is:"+ state);
                inv.click();
                if (!ctx.movement.running() && ctx.movement.energyLevel() > 20)
                    ctx.movement.running(true);
                if(restore1.valid())
                {
                    restore1.interact("Drink");
                dropPot();}
                else if(restore2.valid())
                    restore2.interact("Drink");
                else if(restore3.valid())
                    restore3.interact("Drink");
                else if(restore4.valid())
                    restore4.interact("Drink");
                else
                {
                    System.out.println("out of restores.");
                    bankFlag=true;
                }
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return toInt(prayerPoints)>18;
                    }
                }, 300, 15);

                break;



            case DRINK_ATTACK:
                System.out.println("state is:"+ state);
                if(attack1.valid())
                    attack1.interact("Drink");
                else if(attack2.valid())
                    attack2.interact("Drink");
                else if(attack3.valid())
                    attack3.interact("Drink");
                else if(attack4.valid())
                    attack4.interact("Drink");
                else
                    System.out.println("out of attack.");

                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return ctx.skills.level(Constants.SKILLS_ATTACK)>(ctx.skills.realLevel(Constants.SKILLS_ATTACK)*1.08);
                    }
                }, 300, 15);

                break;
            case DRINK_STR:
                System.out.println("state is:"+ state);

                if(str1.valid())
                    str1.interact("Drink");
                else if(str2.valid())
                    str2.interact("Drink");
                else if(str3.valid())
                    str3.interact("Drink");
                else if(str4.valid())
                    str4.interact("Drink");
                else
                {
                    System.out.println("out of restores.");
                    bankFlag=true;
                }
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return ctx.skills.level(Constants.SKILLS_STRENGTH)>(ctx.skills.realLevel(Constants.SKILLS_STRENGTH)*1.08);
                    }
                }, 300, 15);
                break;

            case HOP:
                System.out.println("state is:"+ state);
                World joinW = ctx.worlds.select().types(World.Type.MEMBERS).joinable().shuffle().peek();
                while(!joinW.hop())
                { System.out.println("waiting for hop cooldown.");
                    Condition.sleep(org.powerbot.script.Random.nextInt(3500, 5000));}
                hopFlag=false;
                worldsHopped++;
                inv.click();

                break;
            case BANK:
                System.out.println("Done");
                ctx.controller.stop();
                break;
            case LOOT:
                System.out.println("state is:" +state);
                if(!loot.inViewport()){
                ctx.movement.step(loot);
                ctx.camera.turnTo(loot);}
                if(ctx.inventory.isFull()) {
                    if (gotPrayer()) {
                        dropPot();
                        Condition.sleep(org.powerbot.script.Random.nextInt(2500, 4000));
                    }
                }

                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        loot.interact(false,"Take");
                        loot =ctx.groundItems.select().id(lootID).nearest().poll();
                        return !loot.valid()||ctx.inventory.isFull();
                    }
                }, 1500, 10);
                break;

            case TURN_PRAYER:
                System.out.println("state isj:"+ state);
                if(toInt(prayerPoints)==0)
                    prayerFlag=true;
                prayerIcon.click();
                if(!activatePrayer.visible())
                    activatePrayer.click();

                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return activatePrayer.visible();
                    }
                }, 300, 10);
                prayerFlag =false;
                inv.click();
                break;
            case REDUCE_HP:
                System.out.println("state is:"+ state);
                if(ctx.skills.level(Constants.SKILLS_HITPOINTS)>1)
                        dwarvenRock.interact(true, "Guzzle");

                break;
            case FIGHT:
                System.out.println("state is:"+ state);
                monster = ctx.npcs.select().id(monstersID).select(new Filter<Npc>(){
                    @Override
                    public boolean accept(Npc npc){
                        return !monster.interacting().valid();
                    }
                    @Override
                    public boolean test(Npc npc){return true;}
                }).nearest().poll();
               if(!monster.inViewport()){
                    ctx.movement.step(monster);
                    ctx.camera.turnTo(monster);
               }
                monster.interact(false,"Attack");
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion();
                    }
                }, 2000, 15);
                break;
            case WAIT:
                System.out.println("state iS:" +state);
                System.out.println("world died is:"+ctx.widgets.component(69,2).text());
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        i++;
                        return i>30||ctx.widgets.component(162,58).component(0).text().contains("wishes to trade with you");
                    }
                }, 1000, 30);
                i=0;
                n = randomNumber.nextInt(58-35)+35;
                while(n>=42 &&n<=50)
                    n = randomNumber.nextInt(58-35)+35;
                ctx.widgets.component(161,n).click();
                break;
        }
    }

    private State getState() {
        if(kills>x5){
            x5 +=15;
            System.out.println("milestone of x5:"+x5 +" hopped:"+worldsHopped);
            hopFlag =true;
            prayerFlag=true;
            if (!ctx.movement.running() && ctx.movement.energyLevel() > 20)
                ctx.movement.running(true);
        }
        updatePotions();
        if(loot.valid())
            return State.LOOT;
        else if(ctx.movement.distance(atLumb)<10&&ctx.movement.distance(atLumb)>=1)
            return State.WAIT;
        else if(hopFlag)
            return State.HOP;
        else if(bankFlag)
            return State.BANK;
        else if(toInt(prayerPoints)<15&&gotPrayer())
            return State.DRINK;
        else if (ctx.skills.level(Constants.SKILLS_STRENGTH)<(ctx.skills.realLevel(Constants.SKILLS_STRENGTH)*1.1)&&gotStr())
            return State.DRINK_STR;
        else if (ctx.skills.level(Constants.SKILLS_ATTACK)<(ctx.skills.realLevel(Constants.SKILLS_STRENGTH)*1.1)&&gotAttack())
            return State.DRINK_ATTACK;
        else if(hp>1&&dwarvenRock.valid())
            return State.REDUCE_HP;
        else if (prayerFlag)
            return State.TURN_PRAYER;
        else if(monster.valid()&&shouldAttack()&&gotPrayer())
            return State.FIGHT;
        else
            return null;

    }


    private enum State{
        DRINK,LOOT,BANK,HOP,FIGHT,DRINK_STR,DRINK_ATTACK,TURN_PRAYER,REDUCE_HP,WAIT
    }

    public void updatePotions(){
        emptyVial = ctx.inventory.select().id(EMPTY_VIAL_ID).poll();
        hp = ctx.skills.level(Constants.SKILLS_HITPOINTS);
        loot =ctx.groundItems.select().id(lootID).nearest().poll();
        restore4 = ctx.inventory.select().id(RESTORE_4_ID).poll();
        restore3 = ctx.inventory.select().id(RESTORE_3_ID).poll();
        restore2 = ctx.inventory.select().id(RESTORE_2_ID).poll();
        restore1 = ctx.inventory.select().id(RESTORE_1_ID).poll();
         str4 = ctx.inventory.select().id(STR_4_ID).poll();
         str3 = ctx.inventory.select().id(STR_3_ID).poll();
         str2 = ctx.inventory.select().id(STR_2_ID).poll();
         str1 = ctx.inventory.select().id(STR_1_ID).poll();
         attack4 = ctx.inventory.select().id(ATTACK_4_ID).poll();
         attack3 = ctx.inventory.select().id(ATTACK_3_ID).poll();
         attack2 = ctx.inventory.select().id(ATTACK_2_ID).poll();
         attack1 = ctx.inventory.select().id(ATTACK_1_ID).poll();
        monster = ctx.npcs.select().id(monstersID).select(new Filter<Npc>(){
            @Override
            public boolean accept(Npc npc){
                return !monster.interacting().valid();
            }
            @Override
            public boolean test(Npc npc){return true;}
        }).nearest().poll();
    }
    public boolean shouldAttack(){return !ctx.players.local().inMotion();}

    public int toInt(Component val){
        try{
            return Integer.parseInt(val.text());
        }catch(NumberFormatException e){
            System.out.println("at the new exception.");
            return 0;
        }
    }
    public boolean gotPrayer(){ return (restore1.valid()|| restore2.valid()||restore3.valid()|| restore4.valid()); }
    public boolean gotAttack(){  return (restore1.valid()|| restore2.valid()||restore3.valid()|| restore4.valid()); }
    public boolean gotStr() {return (restore1.valid()|| restore2.valid()||restore3.valid()|| restore4.valid());}
    public void dropPot(){
            if(emptyVial.valid())
                emptyVial.interact("Drop");
            else if (restore1.valid())
                restore1.interact("Drop");
            else if (restore2.valid())
                restore2.interact("Drop");
            else if (restore3.valid())
                restore3.interact("Drop");
            else if (restore4.valid())
                restore4.interact("Drop");

    }

}
