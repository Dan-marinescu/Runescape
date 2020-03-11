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
        name = "AAAAWea",
        description = "wines from wild",
        properties = "client = 4;"
)
public class Wealth extends PollingScript<ClientContext> implements PaintListener{
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

    final Component magicIcon = ctx.widgets.component(161,57);
    final Component armourIcon = ctx.widgets.component(161,55);
    final Component inventoryIcon = ctx.widgets.component(161,54);
    final Component logoutIcon = ctx.widgets.component(161,45);
    final Component teleGrab = ctx.widgets.component(218,23);
    final Component worldSwitcher = ctx.widgets.component(182,7);
    Component neckComp;
    Component ringComp;

    int tempRingId;
    int tempAmmyId;
    int deaths=0;
    int winesCollected=0;
    Item ring;
    Item staff;
    Item ammy;
    int route =1;

    public static final Tile[] pathToCharge = {new Tile(3347, 3783, 0), new Tile(3347, 3787, 0), new Tile(3347, 3791, 0), new Tile(3347, 3795, 0), new Tile(3347, 3799, 0), new Tile(3347, 3803, 0), new Tile(3347, 3807, 0), new Tile(3346, 3811, 0), new Tile(3346, 3815, 0), new Tile(3346, 3819, 0), new Tile(3346, 3823, 0), new Tile(3346, 3827, 0), new Tile(3345, 3831, 0), new Tile(3345, 3835, 0), new Tile(3345, 3839, 0), new Tile(3345, 3843, 0), new Tile(3345, 3847, 0), new Tile(3345, 3851, 0), new Tile(3345, 3855, 0), new Tile(3345, 3859, 0), new Tile(3345, 3863, 0), new Tile(3345, 3867, 0), new Tile(3348, 3871, 0), new Tile(3352, 3872, 0), new Tile(3356, 3872, 0), new Tile(3357, 3876, 0), new Tile(3361, 3878, 0), new Tile(3365, 3878, 0), new Tile(3369, 3879, 0), new Tile(3373, 3881, 0), new Tile(3376, 3884, 0), new Tile(3377, 3888, 0), new Tile(3373, 3890, 0)};
    public static final Tile[] pathTo30 = {new Tile(3373, 3890, 0), new Tile(3377, 3888, 0), new Tile(3376, 3884, 0), new Tile(3372, 3881, 0), new Tile(3368, 3881, 0), new Tile(3364, 3881, 0), new Tile(3360, 3879, 0), new Tile(3357, 3876, 0), new Tile(3356, 3872, 0), new Tile(3352, 3872, 0), new Tile(3348, 3870, 0), new Tile(3345, 3867, 0), new Tile(3345, 3863, 0), new Tile(3345, 3859, 0), new Tile(3345, 3855, 0), new Tile(3345, 3851, 0), new Tile(3345, 3847, 0), new Tile(3345, 3843, 0), new Tile(3345, 3839, 0), new Tile(3345, 3835, 0), new Tile(3345, 3831, 0), new Tile(3345, 3827, 0), new Tile(3345, 3823, 0), new Tile(3345, 3819, 0), new Tile(3345, 3815, 0), new Tile(3345, 3811, 0), new Tile(3345, 3807, 0), new Tile(3345, 3803, 0), new Tile(3345, 3799, 0), new Tile(3345, 3795, 0), new Tile(3345, 3791, 0), new Tile(3345, 3787, 0), new Tile(3345, 3783, 0), new Tile(3345, 3779, 0), new Tile(3345, 3775, 0), new Tile(3345, 3771, 0), new Tile(3344, 3767, 0), new Tile(3344, 3763, 0), new Tile(3346, 3759, 0), new Tile(3346, 3755, 0)};

    public final Tile  tCharge = new Tile(3373, 3890, 0);

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
            case PATH:
                System.out.println("state is:"+state);
                while(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5)
                {
                    System.out.println("go to charge");
                    walk.walkPath(pathToCharge);
                    if(atArea(tCharge))
                        route =  2;
                }

                break;


            case PATHREV:
                System.out.println("state is:"+state);
                while(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5)
                {
                    System.out.println("going to 30");
                    walk.walkPath(pathTo30);
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
        if(route==1)
            return State.PATH;
        else
            return State.PATHREV;

    }

    private enum State{
        PATH,PATHREV

    }

    public boolean atArea(Tile t){
        return (ctx.movement.distance(t)>=1 && ctx.movement.distance(t)<=10);
    }





}

