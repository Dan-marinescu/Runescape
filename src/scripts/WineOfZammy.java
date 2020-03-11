package scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Interactive;
import org.powerbot.script.rt4.World;

import java.awt.*;

import java.util.concurrent.Callable;

@Script.Manifest(
        name = "AAAWine of zamorak",
        description = "wines from wild",
        properties = "client = 4;"
)
public class WineOfZammy extends PollingScript<ClientContext> implements PaintListener{
    public static final Tile[] pathToBank = {new Tile(3222, 3218, 0), new Tile(3218, 3218, 0), new Tile(3215, 3215, 0), new Tile(3215, 3211, 0), new Tile(3211, 3211, 0), new Tile(3207, 3210, 0), new Tile(3205, 3209, 1), new Tile(3205, 3209, 2), new Tile(3205, 3213, 2), new Tile(3206, 3217, 2), new Tile(3209, 3220, 2)};
    public static final Tile[] pathToWine = {new Tile(3029, 3841, 0), new Tile(3025, 3840, 0), new Tile(3021, 3839, 0), new Tile(3017, 3839, 0), new Tile(3013, 3838, 0), new Tile(3009, 3835, 0), new Tile(3006, 3832, 0), new Tile(3002, 3829, 0), new Tile(2998, 3826, 0), new Tile(2995, 3823, 0), new Tile(2992, 3820, 0), new Tile(2988, 3818, 0), new Tile(2984, 3818, 0), new Tile(2980, 3818, 0), new Tile(2976, 3818, 0), new Tile(2972, 3818, 0), new Tile(2968, 3818, 0), new Tile(2964, 3818, 0), new Tile(2960, 3820, 0), new Tile(2956, 3820, 0), new Tile(2952, 3820, 0)};
    public static final Tile[] pathToLvl30 = {new Tile(2951, 3821, 0), new Tile(2955, 3821, 0), new Tile(2959, 3819, 0), new Tile(2959, 3815, 0), new Tile(2959, 3811, 0), new Tile(2959, 3807, 0), new Tile(2958, 3803, 0), new Tile(2958, 3799, 0), new Tile(2958, 3795, 0), new Tile(2956, 3791, 0), new Tile(2953, 3788, 0), new Tile(2953, 3784, 0), new Tile(2953, 3780, 0), new Tile(2953, 3776, 0), new Tile(2953, 3772, 0), new Tile(2953, 3768, 0), new Tile(2953, 3764, 0), new Tile(2953, 3760, 0), new Tile(2953, 3756, 0)};
    public static final Tile[] pathToWine2 = {new Tile(3027, 3844, 0), new Tile(3025, 3840, 0), new Tile(3021, 3839, 0), new Tile(3017, 3837, 0), new Tile(3014, 3834, 0), new Tile(3010, 3834, 0), new Tile(3006, 3834, 0), new Tile(3002, 3834, 0), new Tile(2998, 3831, 0), new Tile(2994, 3829, 0), new Tile(2990, 3829, 0), new Tile(2986, 3829, 0), new Tile(2982, 3829, 0), new Tile(2978, 3829, 0), new Tile(2974, 3829, 0), new Tile(2970, 3827, 0), new Tile(2967, 3824, 0), new Tile(2963, 3824, 0), new Tile(2959, 3822, 0), new Tile(2955, 3821, 0), new Tile(2951, 3818, 0)};

    final Tile lumb = new Tile(3221,3217,0);
    final Tile GE = new Tile(3163,3477,0);
    final Tile GE2 = new Tile(3161,3484,0);
    final Tile GE3 = new Tile(3162,3489,0);
    final Tile wineSpot = new Tile(2954,3821,0);
    final Tile lumb1 = new Tile(3214,3215,0);
    final Tile lumb2 = new Tile(3212,3210,0);
    final Tile lumb3 = new Tile(3206,3209,0);
    final Tile lvl30 = new Tile(2953,3752,0);
    final Tile church = new Tile(2954,3820,0);
    final Tile lava = new Tile(3029,3841,0);
    final Tile lumbBank = new Tile(3208,3218,2);
    final Tile doorTile = new Tile(2958,3821,0);
    final Tile wine1 = new Tile(2950,3817,0);
    final Tile wine2 = new Tile(2951,3817,0);


    private final int[] wineBounds = {-8,8,-120,-100,-8,8};

    final int RING_5 = 11980;
    final int RING_4 = 11982;
    final int RING_3 = 11984;
    final int RING_2 = 11986;
    final int RING_1 = 11988;
    final int RING_0 = 2572;
    final int BURN_5 = 21166;
    final int BURN_4 = 21169;
    final int BURN_3 = 21171;
    final int BURN_2 = 21173;
    final int BURN_1 = 21175;
    final int LAW_ID = 563;
    final int STAFF_OF_AIR_ID = 1381;
    final int WINE_ON_TABLE = 245;
    final int DOOR = 1521;
    int worldsHopped =0;

    boolean died =false;
    boolean slowDown = false;
    boolean neckFlag =false;
    boolean ringFlag =false;
    boolean staffFlag =false;




    final Component magicIcon = ctx.widgets.component(161,58);
    final Component armourIcon = ctx.widgets.component(161,56);
    final Component inventoryIcon = ctx.widgets.component(161,55);
    final Component logoutIcon = ctx.widgets.component(161,46);
    final Component teleGrab = ctx.widgets.component(218,24);
    final Component worldSwitcher = ctx.widgets.component(182,7);
    Component neckComp;
    Component ringComp;

    int tempRingId;
    int tempAmmyId;
    int deaths=0;
    int winesCollected=0;
    int worldsHoppedx100 =100;
    int counter =5;
    Item ring;
    Item staff;
    Item ammy;

    private final Walker walk = new Walker(ctx);
    java.util.Random randomNumber = new java.util.Random();


    public void start(){

    }


    @Override
    public void poll() {

        final State state = getState();
        if(state ==null) {
            System.out.println("state is:" +state);
            return;
        }
        switch(state){
            case DEATH:
                System.out.println("state is:"+state);
                deaths++;
                died = true;
                ctx.movement.step(lumb1);
                Condition.sleep(org.powerbot.script.Random.nextInt(2500, 3500));
                ctx.movement.step(lumb2);
                Condition.sleep(org.powerbot.script.Random.nextInt(2500, 3500));
                ctx.movement.step(lumb3);
                Condition.sleep(org.powerbot.script.Random.nextInt(2500, 3500));
                while(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5)
                    walk.walkPath(pathToBank);

                break;


            case WALK_TO_WINE:
                System.out.println("state is:" +state);
                while(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5)
                {
                    if(atArea(lumb))
                        break;
                    walk.walkPath(pathToWine2);
                }
            break;

            case WALK_TO_LEVEL_30:
                System.out.println("state is:" +state);
                  while((!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5))//Integer.parseInt(ctx.widgets.component(90, 66).text().replaceAll("Level: ", ""))>30)
                {
                    if(atArea(lumb))
                        break;
                    final GameObject door = ctx.objects.select().id(DOOR).poll();
                    if(door.inViewport()||atArea(church)) {
                         ctx.movement.step(door);
                         ctx.camera.turnTo(door);
                         door.interact("Open");
                    }
                    try {
                        if(Integer.parseInt(ctx.widgets.component(90, 59).text().replaceAll("Level: ", ""))<=30)
                            break;

                    }
                    catch (NumberFormatException n){
                        break;
                    }
                   // checkPkers();
                    walk.walkPath(pathToLvl30);
                }
                break;

            case GRAB_WINE:
                System.out.println("state is:" +state);
                checkPkers();
                final GroundItem wineItem = ctx.groundItems.select().id(WINE_ON_TABLE).each(Interactive.doSetBounds(wineBounds)).poll();
                if(wineItem.valid()) {
                    if(ctx.movement.distance(wine1) != 1 && ctx.movement.distance(wine2) != 1){
                        if(randomNumber.nextInt(2)+1==1)
                            ctx.movement.step(wine1);
                        else
                            ctx.movement.step(wine2);
                    }
                    if(!wineItem.inViewport()){
                        ctx.movement.step(doorTile);
                        ctx.camera.turnTo(wineItem);
                }   magicIcon.click();
                    teleGrab.click();
                    wineItem.interact(true,"Cast");
                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            final GroundItem wineItem = ctx.groundItems.select().id(WINE_ON_TABLE).each(Interactive.doSetBounds(wineBounds)).poll();
                            return !wineItem.valid();
                        }
                    }, 300, 10);
                    winesCollected++;
                }
                else if(slowDown&&!wineItem.valid() && winesCollected%counter==0)
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            final GroundItem wineItem = ctx.groundItems.select().id(WINE_ON_TABLE).each(Interactive.doSetBounds(wineBounds)).poll();
                            checkPkers();
                            return wineItem.valid();
                        }
                    },250,150);
                else{
                    logoutIcon.click();
                    if(ctx.widgets.component(182,7).valid())
                        worldSwitcher.click();
                    ctx.input.send(""+2);
                    Component currentWorldComp = ctx.widgets.component(429,3);
                    String worldString = currentWorldComp.text().replaceAll("[\\D]","");
                    int maxW1,minW2;
                    if(worldString!=""){
                        maxW1 =Integer.parseInt(currentWorldComp.text().replaceAll("[\\D]",""))+20;
                        minW2 =Integer.parseInt(currentWorldComp.text().replaceAll("[\\D]",""))-20;

                    }
                    else {
                        maxW1=530;
                        minW2=480;

                    }
                    final int maxW =maxW1;
                    final int minW =minW2;
                    try{
                    if(ctx.worlds.select().types(World.Type.MEMBERS).select(new Filter<World>() {
                        @Override
                        public boolean accept(World world) {
                            return world.id()>minW&&world.id()<maxW;
                        }
                        @Override
                        public boolean test(World world){return true;}
                    }).joinable().shuffle().peek().hop()){
                        worldsHopped++;
                    }
                    }catch (NumberFormatException e){
                        break;
                    }
                }
                break;

            case TELEPORT_TO_BANK:
                System.out.println("state is:" +state);
                armourIcon.click();
                Component ringComp = ctx.widgets.component(387,15).component(1);
                ringComp.interact(true,"Grand Exchange");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return atArea(GE);
                    }
                },500,5);
                break;

            case BANK_AFTER_TRIP:
                System.out.println("state is:" +state);
                if(atArea(lava))
                    break;
                if(atArea(lumbBank)) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !ctx.players.local().inMotion();
                        }
                    }, 500, 5);
                    if (ctx.worlds.select().types(World.Type.MEMBERS).joinable().shuffle().peek().hop())
                        worldsHopped++;
                }
                bankAfterDeath();
                armourIcon.click();
                neckComp = ctx.widgets.component(387,8).component(1);
                neckComp.interact(true,"Lava Maze");
                if(ctx.inventory.select().id(LAW_ID).count(true)>1){

                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("lava teleport2");
                        return ctx.chat.chatting()||atArea(lava);
                    }
                }, 500, 5);
                ctx.input.send("" + 1);
                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            return atArea(lava);
                        }
                    }, 500, 5);
                }
                break;
        }
    }

    @Override
    public void repaint(Graphics graphics){
        long milliseconds =this.getTotalRuntime();
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60) % 60);
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        Graphics2D g =(Graphics2D)graphics;
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0,0,240,160);

        g.setColor(new Color(255,255,255));
        g.drawRect(0,0,240,160);

        g.drawString("wildy wines by Kaskas",20,20);
        g.drawString("running: "+String.format("%02d:%02d:%02d",hours,minutes,seconds),20,40);
        g.drawString("Wines P/H:"+ (int)(winesCollected*(3600000D/milliseconds)),20,60);
        g.drawString("wines collected:"+ winesCollected,20,80);
        g.drawString("deaths: "+deaths,20,100);
        g.drawString("worlds hopped: "+worldsHopped,20,120);
        g.drawString("current state:"+getState(),20,140);
    }

    private State getState() {
        if (!ctx.movement.running() && ctx.movement.energyLevel() > org.powerbot.script.Random.nextInt(17, 35))
            ctx.movement.running(true);
        if(worldsHopped>worldsHoppedx100) {
            worldsHoppedx100 +=100;
            counter--;
            slowDown = true;
        }
        if(atArea(lumb))
            return State.DEATH;
        else if(atArea(GE)||atArea(GE2)||atArea(GE3)||atArea(lumbBank))
            return State.BANK_AFTER_TRIP;
        else if((ctx.inventory.isFull()||ctx.inventory.select().id(LAW_ID).count(true)==0)&&!atArea(GE)&&!atArea(lvl30))
            return State.WALK_TO_LEVEL_30;
        else if(ctx.widgets.component(90,59).valid()&&(ctx.inventory.isFull()||ctx.inventory.select().id(LAW_ID).count(true)==0))//<=30
            return State.TELEPORT_TO_BANK;
        else if(atArea(wineSpot)&&ctx.inventory.select().id(LAW_ID).count(true)>0)
            return State.GRAB_WINE;
        else if(ctx.widgets.component(90,59).valid()&&(!ctx.inventory.isFull()||ctx.inventory.select().id(LAW_ID).count(true)==0))
            return State.WALK_TO_WINE;
        else
            return null;

    }

    private enum State{
        DEATH,GRAB_WINE,TELEPORT_TO_BANK,WALK_TO_WINE,WALK_TO_LEVEL_30,BANK_AFTER_TRIP
    }

    public boolean atArea(Tile t){
        return (ctx.movement.distance(t)>=1 && ctx.movement.distance(t)<=10);
    }

    public void bankAfterDeath(){
        System.out.println("bank after death func");
        armourIcon.click();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return armourIcon.textureId() != -1 ;
            }
        },100,30);
        armourIcon.click();
        Component neckComp = ctx.widgets.component(387,8,1);
        Component staffComp = ctx.widgets.component(387,9,1);
        Component ringComp = ctx.widgets.component(387,15,1);
        if(!ringComp.visible())
            ringFlag =true;
        if(!neckComp.visible())
            neckFlag =true;
        if(!staffComp.visible())
            staffFlag =true;

        if(ringComp.itemId()==RING_0)
            ringComp.interact("Remove");
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return armourIcon.textureId() != -1 ;
            }
        },100,30);

        ctx.movement.step(ctx.bank.nearest());
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.bank.inViewport();
            }
        },3000,3);
        if(ctx.bank.inViewport()) {
            System.out.println("bank after death here?");
            if (ctx.bank.open()) {
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        System.out.println("waiting for bank open2");
                        ctx.bank.open();
                        return ctx.bank.opened();
                    }
                }, 1000, 10);
            ctx.bank.depositInventory();
            if(outOfSomething()&&ctx.bank.opened()) {
                System.out.println("out of something");
                ctx.controller.stop();
            }
            if(ringFlag) {
                getRing();
                ringFlag = false;
            }
            if(neckFlag) {
                getAmmy();
                neckFlag = false;
            }
            if(staffFlag) {
                ctx.bank.withdraw(STAFF_OF_AIR_ID, 1);
                staffFlag = false;
            }

            if(ctx.inventory.select().id(LAW_ID).count(true)==0)
                ctx.bank.withdraw(LAW_ID,27);
            ctx.bank.close();
            }

            Condition.sleep(org.powerbot.script.Random.nextInt(200, 350));
            inventoryIcon.click();
            equipGear();
            Condition.sleep(org.powerbot.script.Random.nextInt(200, 350));
            armourIcon.click();
            Condition.sleep(org.powerbot.script.Random.nextInt(200, 350));
            if((!neckComp.visible()||!staffComp.visible()||ringComp.itemId()==RING_0||ctx.inventory.select().id(LAW_ID).count(true)==0)&&(!ctx.controller.isStopping())) {
                System.out.println("failed to bank");
                if(atArea(lumbBank))
                    ctx.movement.step(lumbBank);
                bankAfterDeath();
            }

        }
        else{
            ctx.movement.step(ctx.bank.nearest());
            ctx.camera.turnTo(ctx.bank.nearest());
            ctx.bank.open();
        }
    }

    public void getRing(){
        if(ctx.bank.select().id(RING_1).poll().stackSize()>0)
            ctx.bank.withdraw(RING_1,1);
        else if(ctx.bank.select().id(RING_2).poll().stackSize()>0)
            ctx.bank.withdraw(RING_2,1);
        else if(ctx.bank.select().id(RING_3).poll().stackSize()>0)
            ctx.bank.withdraw(RING_3,1);
        else if(ctx.bank.select().id(RING_4).poll().stackSize()>0)
            ctx.bank.withdraw(RING_4,1);
        else
            ctx.bank.withdraw(RING_5,1);


    }

    public void getAmmy(){
        if(ctx.bank.select().id(BURN_1).poll().stackSize()>0)
            ctx.bank.withdraw(BURN_1,1);
        else if(ctx.bank.select().id(BURN_2).poll().stackSize()>0)
            ctx.bank.withdraw(BURN_2,1);
        else if(ctx.bank.select().id(BURN_3).poll().stackSize()>0)
            ctx.bank.withdraw(BURN_3,1);
        else if(ctx.bank.select().id(BURN_4).poll().stackSize()>0)
            ctx.bank.withdraw(BURN_4,1);
        else
            ctx.bank.withdraw(BURN_5,1);

    }

    public boolean gotRingOrAmmy(){
        int j=0;
        boolean gotAmmy =false,gotRing=false;
        for(int i=0;i<=10;i+=2) {
            if (ctx.bank.select().id(BURN_5 + i + j).poll().stackSize() >= 1)
                gotAmmy=true;
            if (ctx.bank.select().id(RING_5 + i).poll().stackSize() >= 1)
                gotRing=true;
            j=1;}
        return gotAmmy&&gotRing;
    }

    public boolean outOfSomething(){
        return ctx.bank.select().id(LAW_ID).count(true)==0||ctx.bank.select().id(STAFF_OF_AIR_ID).poll().stackSize()==0 || !gotRingOrAmmy();
    }

    public void checkPkers(){
        Component currentWorldComp = ctx.widgets.component(429,3);
        int wildLevel;
        World joinW;
        String worldString = currentWorldComp.text().replaceAll("[\\D]","");
        int maxW1,minW2;
        if(worldString!=""){
             maxW1 =Integer.parseInt(currentWorldComp.text().replaceAll("[\\D]",""))+20;
             minW2 =Integer.parseInt(currentWorldComp.text().replaceAll("[\\D]",""))-20;

          }
        else {
             maxW1=530;
             minW2=480;

        }
        final int maxW =maxW1;
        final int minW =minW2;
        for(Player p: ctx.players.select().within(300)) {
            if(ctx.widgets.component(90,59).valid())
                try {
                    wildLevel = Integer.parseInt(ctx.widgets.component(90, 59).text().replaceAll("Level: ", ""));
                }
                catch (NumberFormatException n){
                    break;
                }
            else
                wildLevel = 0;
            try {
                if (p.combatLevel() >= Math.abs(wildLevel - ctx.players.local().combatLevel()) && p.combatLevel() <= wildLevel + ctx.players.local().combatLevel() && !(p.name()).equals(ctx.players.local().name())) {
                    joinW = ctx.worlds.select().types(World.Type.MEMBERS).select(new Filter<World>() {
                        @Override
                        public boolean accept(World world) {
                            return world.id() > minW && world.id() < maxW;
                        }

                        @Override
                        public boolean test(World world) {
                            return true;
                        }
                    }).joinable().shuffle().peek();
                    System.out.println("pker!!! " + p.name() + " " + p.combatLevel());
                    logoutIcon.click();
                    if (ctx.widgets.component(182, 7).valid())
                        worldSwitcher.click();
                    ctx.input.send("" + 2);
                    if(joinW.hop())
                        worldsHopped++;
                }
            }catch(NumberFormatException n){
                if(ctx.worlds.select().types(World.Type.MEMBERS).joinable().shuffle().peek().hop())
                    worldsHopped++;

            }


        }
    }

    public void equipGear(){
        //myW f2p
        System.out.println("equip gear func");
        tempRingId = ctx.widgets.component(149,0).itemIds()[0];
        tempAmmyId = ctx.widgets.component(149,0).itemIds()[1];
        ring = ctx.inventory.select().id(tempRingId).poll();
        staff = ctx.inventory.select().id(STAFF_OF_AIR_ID).poll();
        ammy = ctx.inventory.select().id(tempAmmyId).poll();
        ring.interact("Wear");
        Condition.sleep(org.powerbot.script.Random.nextInt(100, 150));
        staff.interact("Wield");
        Condition.sleep(org.powerbot.script.Random.nextInt(100, 150));
        ammy.interact("Wear");
        Condition.sleep(org.powerbot.script.Random.nextInt(100, 150));
    }




}