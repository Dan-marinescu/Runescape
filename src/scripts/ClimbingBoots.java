package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;

import javax.swing.plaf.synth.SynthStyle;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "Climbing",
        description = "buying Climbing boots",
        properties = "client = 4;"
)
public class ClimbingBoots extends PollingScript<ClientContext> {
    final int COINS_ID = 995;
    final int RING_ID = 2552;
    final int NECKLACES_ID=3853;
    final int BOOTS_ID = 3105;
    final int OPEN_GATE_ID = 3728;
    final int CLOSED_GATE_ID = 3726;
    final int DOOR_ID = 3745;
    final int TENZING_ID = 4094;


     int totalCoins =10000;
     int necklaces=1;
     int rings =1;
     int totalBoots=0;
     int attempt =0;

     boolean ringsFlag = false;
     boolean necksFlag = false;

     Component ringComp = ctx.widgets.component(387,15).component(1);
     Component neckComp = ctx.widgets.component(387,8).component(1);
     final Component armour = ctx.widgets.component(161,55);
     final Component inv = ctx.widgets.component(161,54);

     Item ring;
     Item necklace;

     GameObject closedGate = ctx.objects.select().id(CLOSED_GATE_ID).poll();
     GameObject openedGate = ctx.objects.select().id(OPEN_GATE_ID).poll();
     GameObject door = ctx.objects.select().id(DOOR_ID).poll();

     final Npc tenzing = ctx.npcs.select().id(TENZING_ID).nearest().poll();

     private final Walker walk = new Walker(ctx);
     public static final Tile[] path = {new Tile(2897, 3553, 0), new Tile(2898, 3549, 0), new Tile(2895, 3546, 0), new Tile(2891, 3546, 0), new Tile(2887, 3546, 0), new Tile(2884, 3549, 0), new Tile(2881, 3552, 0), new Tile(2878, 3555, 0), new Tile(2875, 3558, 0), new Tile(2872, 3561, 0), new Tile(2869, 3564, 0), new Tile(2865, 3566, 0), new Tile(2861, 3566, 0), new Tile(2857, 3566, 0), new Tile(2854, 3569, 0), new Tile(2851, 3572, 0), new Tile(2848, 3576, 0), new Tile(2845, 3579, 0), new Tile(2841, 3580, 0), new Tile(2837, 3582, 0), new Tile(2833, 3583, 0), new Tile(2829, 3583, 0), new Tile(2826, 3580, 0), new Tile(2827, 3576, 0), new Tile(2830, 3573, 0), new Tile(2833, 3570, 0), new Tile(2830, 3566, 0), new Tile(2827, 3563, 0), new Tile(2829, 3559, 0), new Tile(2826, 3556, 0), new Tile(2822, 3555, 0)};
     Tile northTile = new Tile(2824,3555,0);
     Tile southTile = new Tile(2824,3554,0);
     Tile inside = new Tile(2822,3555,0);
     Tile tenzingHouse = new Tile(2822,3555,0);
     Tile castleWars = new Tile(2440,3089,0);
     Tile burthorpe = new Tile(2900,3551,0);
     Tile CameraHouse = new Tile(8544,-2103,6563);
     Tile stuckTile = new Tile(2829,3558,0);
     Tile stuckTile2 = new Tile(2828,3559,0);
     java.util.Random randomNumber = new java.util.Random();


    public void start(){

    }


    @Override
    public void poll() {
        if(totalCoins<10000||necklaces<1||rings<1) {
            System.out.println("out of coins necks or rings."+totalCoins+" "+necklaces  +" "+ rings);
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

                neckComp = ctx.widgets.component(387,8).component(1);
                ringComp =ctx.widgets.component(387,15).component(1);
                if(ringComp.itemId()==-1)
                    ringsFlag=true;
                if(neckComp.itemId()==-1)
                    necksFlag=true;
                if(ctx.bank.inViewport()) {
                    if (ctx.bank.open()) {
                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                System.out.println("waiting for bank open");
                                return ctx.bank.opened();
                            }
                        }, 500, 100);
                    }
                    ctx.bank.depositAllExcept(COINS_ID);
                    if(ctx.inventory.select().id(COINS_ID).count(true)<3000)
                        ctx.bank.withdraw(COINS_ID,20000);
                    if(ringsFlag){
                        ctx.bank.withdraw(RING_ID,1);
                        ringsFlag=false;
                    }

                    if(necksFlag){
                        ctx.bank.withdraw(NECKLACES_ID,1);
                        necksFlag=false;
                    }

                    totalCoins = ctx.bank.select().id(COINS_ID).poll().stackSize();
                    necklaces = ctx.bank.select().id(NECKLACES_ID).poll().stackSize();
                    rings = ctx.bank.select().id(RING_ID).poll().stackSize();
                    totalBoots = ctx.bank.select().id(BOOTS_ID).poll().stackSize();
                    ctx.bank.close();
                    ring = ctx.inventory.select().id(RING_ID).poll();
                    necklace = ctx.inventory.select().id(NECKLACES_ID).poll();
                    if(ctx.inventory.select().id(RING_ID).count(true)==1||ctx.inventory.select().id(NECKLACES_ID).count(true)==1) {
                        inv.click();
                        ring.interact("Wear");
                        necklace.interact("Wear");
                    }

                }
                else{
                    ctx.movement.step(ctx.bank.nearest());
                    ctx.camera.turnTo(ctx.bank.nearest());
                }
                break;

            case TELEPORBURTHORPE:
                System.out.println("state is:" +state);
                armour.click();
                neckComp.interact(true,"Burthorpe");
                Condition.sleep(org.powerbot.script.Random.nextInt(3500, 4000));
                break;

            case TELEPORTBANK:
                System.out.println("state is:" +state);
                if (!ctx.movement.running() && ctx.movement.energyLevel() > 20)
                    ctx.movement.running(true);
                armour.click();
                ringComp.interact(true,"Castle Wars");
                Condition.sleep(org.powerbot.script.Random.nextInt(3500, 4000));
                break;

            case WALK:
                System.out.println("state is:" +state);


                while(!ctx.players.local().inMotion()||ctx.movement.destination().equals(Tile.NIL)||ctx.movement.destination().distanceTo(ctx.players.local())<5||ctx.movement.distance(stuckTile)==1||ctx.movement.distance(stuckTile2)==1)
                {

                    if(ctx.movement.distance(stuckTile)==1||ctx.movement.distance(stuckTile2)==1)
                    {
                        System.out.println("stuck?");
                        ctx.movement.step(northTile);
                    }

                    if(ctx.movement.distance(tenzingHouse)<2&&ctx.movement.distance(tenzingHouse)!= -1)
                      break;

                    else{
                    if(!ctx.players.local().inMotion()) {
                     ctx.camera.pitch(org.powerbot.script.Random.nextInt(5, 96));
                     ctx.camera.angle('w');
                    }
                    walk.walkPath(path);
                }
                }

                break;

            case BUYING:
                System.out.println("state is:" +state);
                if(!ctx.chat.chatting()) {
                    System.out.println("not chatting so calling tenz");
                    ctx.npcs.select().name("Tenzing").poll().interact(false, "Talk-to");
                    Condition.sleep(org.powerbot.script.Random.nextInt(400, 700));
                }
                while(ctx.chat.chatting()&&!ctx.inventory.isFull()) {
                    if(!ctx.chat.canContinue())
                        ctx.input.sendln("" + 1);
                    else
                        ctx.chat.clickContinue(true);
                }

        }
    }

    private State getState() {
        if(ctx.inventory.isFull()&&ctx.movement.distance(castleWars)<20&&ctx.movement.distance(castleWars)!=-1)//got wanted amount of planks, time to trade and trader is here
            return State.BANK;
        else if(ctx.movement.distance(burthorpe)>=1||ctx.players.local().inMotion())//im at cammy and got logs
            return State.WALK;
        else if(ctx.movement.distance(castleWars)<20&&!ctx.inventory.isFull()&&ctx.movement.distance(castleWars)!=-1)//im at cammy and got no logs in inv
            return State.TELEPORBURTHORPE;
        else if (ctx.movement.distance(tenzingHouse)<6&&ctx.movement.distance(tenzingHouse)!=-1&&!ctx.inventory.isFull())//im at house and got logs
            return State.BUYING;
        else if(ctx.movement.distance(tenzingHouse)<5&&ctx.movement.distance(tenzingHouse)!=-1&&ctx.inventory.isFull())//no more logs and at house
            return State.TELEPORTBANK;

        return null;
    }
    private enum State{
        BANK,TELEPORBURTHORPE,TELEPORTBANK,WALK,BUYING
    }

}
