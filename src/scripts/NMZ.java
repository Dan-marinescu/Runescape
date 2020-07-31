package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;


import javax.swing.plaf.synth.SynthStyle;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "NMZ",
        description = "NMZ",
        properties = "client = 4;"
)
public class NMZ extends PollingScript<ClientContext> {
    java.util.Random randomNumber = new java.util.Random();



    final int DWARVEN_ROCK_ID = 7510;
    final int RESTORE_4_ID = 2434; //3024
    final int RESTORE_3_ID = 139; //3026
    final int RESTORE_2_ID = 141; //3028
    final int RESTORE_1_ID = 143; //3030
    final int ABS_4_ID = 11734;
    final int ABS_3_ID = 11735;
    final int ABS_2_ID = 11736;
    final int ABS_1_ID = 11737;
    final int OVL_4_ID = 11730;
    final int OVL_3_ID = 11731;
    final int OVL_2_ID = 11732;
    final int OVL_1_ID = 11733;
    final int RANGE_4_ID = 11722;
    final int RANGE_3_ID = 11723;
    final int RANGE_2_ID = 11724;
    final int RANGE_1_ID = 11725;
    final int MAGE_4_ID = 11726;
    final int MAGE_3_ID = 11727;
    final int MAGE_2_ID = 11728;
    final int MAGE_1_ID = 11729;
    final int ZAPPER_ID = 26256;
    final int ACTIVE_ZAPPER_ID = 30343;
    final int POWER_SURGE_ID = 26264;
    final int RECURRENT_DAMAGE_ID = 26265;

    int randomPPoints =randomNumber.nextInt(7)+15;
    int randomAbs =randomNumber.nextInt(500)+250;

    int hpXp;
    int Xp;
    int initX;
    int initY;
    int specialCost=110;
    int firstXp;
    int progressXp;
    int x5=5000;

    boolean flag=true;
    boolean firstHit = true;
    boolean firstSpecial = true;

    int hp = ctx.skills.level(Constants.SKILLS_HITPOINTS);
    Component prayerPoints = ctx.widgets.component(160,15);
    Component LogoutIcon = ctx.widgets.component(161,41);
    Component LogoutButton = ctx.widgets.component(182,12);
    Component abs = ctx.widgets.component(202,3,5);
    Component prayerIcon = ctx.widgets.component(161,59);
    Component activatePrayer = ctx.widgets.component(541,19,0);
    Component inventoryIcon = ctx.widgets.component(161,57);
    Component boostRange = ctx.widgets.component(320,4,3);
    Component basicRange = ctx.widgets.component(320,4,4);
    Component specialValid = ctx.widgets.component(160,30);
    Component specialprecent = ctx.widgets.component(160,31);
    Component specialActive = ctx.widgets.component(160,32);
    Component xpBar = ctx.widgets.component(122,9);

    Item dwarvenRock = ctx.inventory.select().id(DWARVEN_ROCK_ID).poll();
    Item restore4 = ctx.inventory.select().id(RESTORE_4_ID).poll();
    Item restore3 = ctx.inventory.select().id(RESTORE_3_ID).poll();
    Item restore2 = ctx.inventory.select().id(RESTORE_2_ID).poll();
    Item restore1 = ctx.inventory.select().id(RESTORE_1_ID).poll();
    Item abs4 = ctx.inventory.select().id(ABS_4_ID).poll();
    Item abs3 = ctx.inventory.select().id(ABS_3_ID).poll();
    Item abs2 = ctx.inventory.select().id(ABS_2_ID).poll();
    Item abs1 = ctx.inventory.select().id(ABS_1_ID).poll();
    Item overload4 = ctx.inventory.select().id(OVL_4_ID).poll();
    Item overload3 = ctx.inventory.select().id(OVL_3_ID).poll();
    Item overload2 = ctx.inventory.select().id(OVL_2_ID).poll();
    Item overload1 = ctx.inventory.select().id(OVL_1_ID).poll();
    Item range4 = ctx.inventory.select().id(RANGE_4_ID).poll();
    Item range3 = ctx.inventory.select().id(RANGE_3_ID).poll();
    Item range2 = ctx.inventory.select().id(RANGE_2_ID).poll();
    Item range1 = ctx.inventory.select().id(RANGE_1_ID).poll();
    Item mage4 = ctx.inventory.select().id(MAGE_4_ID).poll();
    Item mage3 = ctx.inventory.select().id(MAGE_3_ID).poll();
    Item mage2 = ctx.inventory.select().id(MAGE_2_ID).poll();
    Item mage1 = ctx.inventory.select().id(MAGE_1_ID).poll();


    GameObject zapper = ctx.objects.select().id(ZAPPER_ID).poll();
    GameObject activeZapper = ctx.objects.select().id(ACTIVE_ZAPPER_ID).poll();
    GameObject powerSurge = ctx.objects.select().id(POWER_SURGE_ID).poll();
    GameObject recurrentDamage = ctx.objects.select().id(RECURRENT_DAMAGE_ID).poll();

    Tile outSide = new Tile(2608,3115,0);
    Tile middle;

    public void start(){
        initX=ctx.players.local().tile().x();
        initY=ctx.players.local().tile().y();
        middle = new Tile(initX-org.powerbot.script.Random.nextInt(2,6),initY+org.powerbot.script.Random.nextInt(15,19),3);
        Xp = ctx.skills.experience(Constants.SKILLS_STRENGTH

        );
        hpXp = ctx.skills.experience(Constants.SKILLS_HITPOINTS);
        specialCost = (ctx.widgets.component(593,1).text().equals("Dorgeshuun crossbow"))?110:25;
        System.out.println(specialCost);




    }

    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {
            System.out.println("state is:" +state);
            return;
        }

        switch(state){
            case PRAYER:
                //System.out.println("state is:" +state);
                inventoryIcon.click();
                if(restore1.valid())
                    restore1.interact("Drink");
                else if(restore2.valid())
                    restore2.interact("Drink");
                else if(restore3.valid())
                    restore3.interact("Drink");
                else if(restore4.valid())
                    restore4.interact("Drink");
                else
                    System.out.println("out of restores.");
                randomPPoints =randomNumber.nextInt(7)+15;
                break;

            case HEAL:
                //System.out.println("state is:" +state);
                if(abs1.valid())
                    abs1.interact("Drink");
                else if(abs2.valid())
                    abs2.interact("Drink");
                else if(abs3.valid())
                    abs3.interact("Drink");
                else if(abs4.valid())
                    abs4.interact("Drink");
                else
                    System.out.println("out of abs.");
                randomAbs =randomNumber.nextInt(500)+250;
                break;

            case ORB:
                //System.out.println("state is:" +state);
                if(zapper.valid())
                {if(rotateToOrb(zapper))
                    Condition.sleep(org.powerbot.script.Random.nextInt(13500, 14000));}
                if(powerSurge.valid())
                    rotateToOrb(powerSurge);
                if(recurrentDamage.valid())
                    rotateToOrb(recurrentDamage);
                Condition.sleep(org.powerbot.script.Random.nextInt(3500, 4000));
                break;

            case REPOSITION:
                //System.out.println("state is:" +state);
                if (!ctx.movement.running() && ctx.movement.energyLevel() > 20)
                    ctx.movement.running(true);

                ctx.movement.step(middle);
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("Reposition");
                        ctx.movement.step(middle);
                        return ctx.movement.distance(middle)==1;
                    }
                }, 5000, 10);
                break;

            case DRINK_OVL:
                //System.out.println("state is:" +state);
                drink_ovl();

                break;

            case DRINK_RANGE:
                //System.out.println("state is:" +state);
                if(range1.valid())
                    range1.interact("Drink");
                else if(range2.valid())
                    range2.interact("Drink");
                else if(range3.valid())
                    range3.interact("Drink");
                else
                    range4.interact("Drink");
                Condition.sleep(org.powerbot.script.Random.nextInt(4200, 5300));
                break;

            case DRINK_MAGE:
                //System.out.println("state is:" +state);
                if(mage1.valid())
                    mage1.interact("Drink");
                else if(mage2.valid())
                    mage2.interact("Drink");
                else if(mage3.valid())
                    mage3.interact("Drink");
                else
                    mage4.interact("Drink");
                Condition.sleep(org.powerbot.script.Random.nextInt(4200, 5300));
                break;


            case LOGOUT:
                System.out.println("state is:" + state);
                LogoutIcon.click();
                Condition.sleep(15000);
                LogoutButton.click();
                ctx.controller.stop();

                break;

            case REDUCE_HP:
                //System.out.println("state is:" + state);
                    if(hp>50&&gotOvls())
                    {
                        System.out.println("reduce hp loop and hp is over 50.");
                        System.out.println("hp is:"+ hp + "and got ovls is:"+gotOvls());
                        drink_ovl();
                        break;
                    }
                    else
                        dwarvenRock.interact(true, "Guzzle");


                break;

            case FIGHT:
                //System.out.println("state is:"+state);

                //Pray flick
//                if(ctx.prayer.prayerPoints()>0&&!gotPrayer()){
//                    ctx.prayer.quickPrayer(true);
//                    Condition.sleep(org.powerbot.script.Random.nextInt(100, 250));
//                    ctx.prayer.quickPrayer(false);
//                }

                while(specialValid.visible()&&specialCost<=toInt(specialprecent)){
                    if(ctx.movement.distance(outSide)==1)
                        break;
                    if (!ctx.combat.specialAttack())
                        specialActive.click();

                }
                Condition.sleep(org.powerbot.script.Random.nextInt(1500, 3000));
                break;

            case TURN_PRAYER:
                System.out.println("state is:"+ state);
                prayerIcon.click();
                if(!activatePrayer.visible())
                    activatePrayer.click();
                inventoryIcon.click();
                flag=false;
                break;
        }
    }

    private State getState() {
        updatePotions();
        if (ctx.movement.distance(outSide)==1)
            return State.LOGOUT;
        else if(gotAbs() && toInt(abs)<randomAbs)
            return State.HEAL;
        else if(toInt(prayerPoints)<randomPPoints&&gotPrayer())
            return State.PRAYER;
        else if(flag&&toInt(abs)<randomPPoints)
            return State.TURN_PRAYER;
        else if(gotOrb())
            return State.ORB;
        else if(ctx.movement.distance(middle)>5)
            return State.REPOSITION;
        else if (hp>50&&gotOvls())
            return State.DRINK_OVL;
        else if (ctx.skills.level(Constants.SKILLS_RANGE)<(ctx.skills.realLevel(Constants.SKILLS_RANGE)*1.07)&&gotRange())
            return State.DRINK_RANGE;
        else if (ctx.skills.level(Constants.SKILLS_MAGIC)<(ctx.skills.realLevel(Constants.SKILLS_MAGIC)*1.07)&&gotMage())
            return State.DRINK_MAGE;
        else if (hp>1&&dwarvenRock.valid())
            return State.REDUCE_HP;
        else
            return State.FIGHT;

    }
    private enum State{
        ORB,HEAL,DRINK_OVL,REPOSITION,LOGOUT,REDUCE_HP,PRAYER,FIGHT,TURN_PRAYER,DRINK_RANGE,DRINK_MAGE
    }

    public boolean gotOrb(){
        return (zapper.valid()||(powerSurge.valid()&&specialValid.visible())||recurrentDamage.valid());
    }


    public boolean gotPrayer(){
        return (restore1.valid()|| restore2.valid()||restore3.valid()|| restore4.valid());
    }

    public boolean gotAbs(){
        return (abs1.valid()||abs2.valid() || abs3.valid()|| abs4.valid());
    }

    public boolean gotOvls(){
        return (overload1.valid()||overload2.valid() || overload3.valid()|| overload4.valid());
    }

    public boolean gotRange(){
        return (range1.valid()||range2.valid() ||range3.valid()|| range4.valid());
    }

    public boolean gotMage(){
        return (mage1.valid()||mage2.valid() ||mage3.valid()|| mage4.valid());
    }

    public void drink_ovl(){
        updatePotions();
        if(overload1.valid())
            overload1.interact("Drink");
        else if(overload2.valid())
            overload2.interact("Drink");
        else if(overload3.valid())
            overload3.interact("Drink");
        else
            overload4.interact("Drink");
        Condition.sleep(org.powerbot.script.Random.nextInt(9000, 10000));

    }



    public int toInt(Component val){
        try{
            return Integer.parseInt(val.text());
        }catch(NumberFormatException e){
            System.out.println("at the new exception.");
            return 0;
        }


    }

    public void updatePotions(){
         hp = ctx.skills.level(Constants.SKILLS_HITPOINTS);
         restore4 = ctx.inventory.select().id(RESTORE_4_ID).poll();
         restore3 = ctx.inventory.select().id(RESTORE_3_ID).poll();
         restore2 = ctx.inventory.select().id(RESTORE_2_ID).poll();
         restore1 = ctx.inventory.select().id(RESTORE_1_ID).poll();
         abs4 = ctx.inventory.select().id(ABS_4_ID).poll();
         abs3 = ctx.inventory.select().id(ABS_3_ID).poll();
         abs2 = ctx.inventory.select().id(ABS_2_ID).poll();
         abs1 = ctx.inventory.select().id(ABS_1_ID).poll();
         overload4 = ctx.inventory.select().id(OVL_4_ID).poll();
         overload3 = ctx.inventory.select().id(OVL_3_ID).poll();
         overload2 = ctx.inventory.select().id(OVL_2_ID).poll();
         overload1 = ctx.inventory.select().id(OVL_1_ID).poll();
         range4 = ctx.inventory.select().id(RANGE_4_ID).poll();
         range3 = ctx.inventory.select().id(RANGE_3_ID).poll();
         range2 = ctx.inventory.select().id(RANGE_2_ID).poll();
         range1 = ctx.inventory.select().id(RANGE_1_ID).poll();
         mage4 = ctx.inventory.select().id(MAGE_4_ID).poll();
         mage3 = ctx.inventory.select().id(MAGE_3_ID).poll();
         mage2 = ctx.inventory.select().id(MAGE_2_ID).poll();
         mage1 = ctx.inventory.select().id(MAGE_1_ID).poll();
         zapper = ctx.objects.select().id(ZAPPER_ID).poll();
         activeZapper = ctx.objects.select().id(ACTIVE_ZAPPER_ID).poll();
         powerSurge = ctx.objects.select().id(POWER_SURGE_ID).poll();
         recurrentDamage = ctx.objects.select().id(RECURRENT_DAMAGE_ID).poll();
         boostRange = ctx.widgets.component(320,4,3);
         basicRange = ctx.widgets.component(320,4,4);
    }

    public boolean rotateToOrb(GameObject orb){
        ctx.movement.step(orb);
        if(!orb.inViewport())
            ctx.camera.turnTo(orb);
        return orb.interact(false,"Activate");
    }

}
