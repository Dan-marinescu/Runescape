package scripts;



import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Interactive;
import org.powerbot.script.rt4.World;

import javax.swing.plaf.synth.SynthStyle;
import java.awt.*;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "Testing",
        description = "wines from wild",
        properties = "client = 4;"
)
public class Testing extends PollingScript<ClientContext> implements PaintListener{
    Component logoutIcon = ctx.widgets.component(161,46);
    final Component worldSwitcher = ctx.widgets.component(182,7);
    final Component armourIcon = ctx.widgets.component(161,56);
    Random random = new Random();
    int newX;
    int newY;
    boolean reLocation = false;
    final Tile geTile = new Tile(3173,3481,0);
    final Tile safeTile = new Tile(3178,3481,0);
    final Tile GE = new Tile(3163,3477,0);

    final Tile lumb = new Tile(3221,3217,0);
    final int RING_5 = 11980;
    final int RING_4 = 11982;
    final int RING_3 = 11984;
    final int RING_2 = 11986;
    final int RING_1 = 11988;
    Queue<Tile> tq = new LinkedList<Tile>();
    Queue<Long> timeQ = new LinkedList<Long>();
    Queue<String> nameQ = new LinkedList<String>();

    public void start(){

    }


    @Override
    public void poll() {

        final State state = getState();
        if (state == null) {
            System.out.println("state is:" + state);
            return;
        }

        switch (state) {
            case TESTING:
                System.out.println("State is:" + state);
                Component currentWorldComp = ctx.widgets.component(429,3);
                int wildLevel;
                World joinW;
                String worldString = currentWorldComp.text().replaceAll("[\\D]","");
                int maxW1,minW2;
                if(worldString!=""){
                    maxW1 =Integer.parseInt(currentWorldComp.text().replaceAll("[\\D]",""))+10;
                    minW2 =Integer.parseInt(currentWorldComp.text().replaceAll("[\\D]",""))-10;

                }
                else {
                    maxW1=525;
                    minW2=491;

                }
                final int maxW =maxW1;
                final int minW =minW2;
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
                    System.out.println("pker!!!");
                    logoutIcon.click();
                    if (ctx.widgets.component(182, 7).valid())
                        worldSwitcher.click();
                    ctx.input.send("" + 2);
                    joinW.hop();
                    reLocation = true;
                    break;

            case NOTHING:
                System.out.println("state is:" + state);
                int myX = ctx.players.local().tile().x();
                int myY = ctx.players.local().tile().y();
                newX = myX;
                newY = myY;
                while(newX == myX && newY == myY) {
                    newX = random.nextInt(6) + 3233;
                    newY = random.nextInt(6) + 3147;
                }
                System.out.println(ctx.players.local().tile()+" "+ newX + newY);
                final Tile newT = new Tile(newX,newY,0);
                ctx.movement.step(newT);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.movement.distance(newT) == 1 || ctx.players.select().within(50).size() > 1;
                    }
                },50,100);
                reLocation = false;
                break;


                case SCAN:
                    System.out.println("State is:"+ state);
                    if(getTime()%50 == 0 )
                        ctx.input.send("2");
                    for(Player p: ctx.players.within(50)){
                            if(p.healthPercent() ==0 || p.animation() ==836 && !nameQ.contains(p.name())) {
                                tq.add(p.tile());
                                timeQ.add((this.getTotalRuntime() / 1000));
                                nameQ.add(p.name());
                            }

                    }
                    break;


            case LOOT:
                System.out.println("State is:"+ state);
                ctx.movement.step(tq.peek());
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.movement.distance(tq.peek()) == 1;
                    }
                },50,100);
                tq.poll();
                while(timeQ.peek() + 50 < getTime() && timeQ.peek() + 65 > getTime()  && ctx.inventory.select().count()!= 28 || ctx.groundItems.select().within(0).poll().id() != -1) {
                    ctx.groundItems.select().within(0).poll().click();
                    System.out.println("looting!");
                }
                timeQ.poll();
                ctx.movement.step(safeTile);
                break;

                case BANK:
                    System.out.println("State is:"+ state);
                    ctx.movement.step(geTile);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.movement.distance(geTile)<2;
                        }
                    },400,15);
                    if (ctx.bank.inViewport()) {
                        Condition.wait(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                ctx.bank.open();
                                return ctx.bank.opened();
                            }
                        }, 1000, 10);
                    }
                    else{
                        ctx.camera.turnTo(ctx.bank.nearest());
                        ctx.movement.step(ctx.bank.nearest());
                    }
                    if(ctx.bank.opened()){
                        ctx.bank.depositInventory();
                        ctx.bank.close();
                    }
                    ctx.movement.step(geTile);
                    break;

            case RETURNGE:
                if (ctx.bank.inViewport()) {
                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() throws Exception {
                            ctx.bank.open();
                            return ctx.bank.opened();
                        }
                    }, 1000, 10);
                }
                else{
                    ctx.camera.turnTo(ctx.bank.nearest());
                    ctx.movement.step(ctx.bank.nearest());
                }

                if(ctx.bank.opened()){
                    if(gotRing()) {
                        getRing();
                        ctx.bank.close();
                        equipGear();
                        armourIcon.click();
                        Component ringComp = ctx.widgets.component(387,15).component(1);
                        ringComp.interact(true,"Grand Exchange");
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return atArea(GE);
                            }
                        },500,5);
                        ctx.movement.step(geTile);
                    }
                    else {
                        ctx.bank.close();
                        ctx.controller.stop();
                    }
                }
                break;
        }
}
        private State getState () {
            if(ctx.players.local().animation() ==424)
                ctx.movement.step(safeTile);
            if (atArea(lumb))
                return State.RETURNGE;
            else if(ctx.inventory.select().count()>14 && timeQ.peek() + 30 < getTime())
                return State.BANK;
            else if(!timeQ.isEmpty() && timeQ.peek()+53< getTime() && ctx.inventory.select().count()!= 28)
                return State.LOOT;
            else if (true)
                return State.SCAN;
            else if(ctx.players.select().within(50).size() < 1)
                return State.TESTING;
            else if (reLocation)
                return State.NOTHING;
            else
                return State.TELEPORBURTHORPE;

        }
        private enum State {
            DEATH, TELEPORBURTHORPE, TELEPORTBANK, WALK_TO_WINE, WALK_TO_LEVEL_30,TESTING,NOTHING,TESTSPELL,LOOT,SCAN,BANK,RETURNGE
        }


        public long getTime(){
            return (this.getTotalRuntime() / 1000);
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

        g.drawString("wildy wines by Kaskas",20,20);
        g.drawString("running: "+seconds,20,40);
        g.drawString("tiles:"+tq,20,60);
        g.drawString("timer:"+timeQ,20,80);
        g.drawString("state is:"+getState(),20,120);
    }
    public boolean atArea(Tile t){
        return (ctx.movement.distance(t)>=1 && ctx.movement.distance(t)<=10);
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
    public boolean gotRing(){
        int j=0;
        boolean gotAmmy =false,gotRing=false;
        for(int i=0;i<=10;i+=2) {
            if (ctx.bank.select().id(RING_5 + i).poll().stackSize() >= 1)
                gotRing=true;
        }
        return gotRing;
    }

    public void equipGear(){
        //myW f2p
        System.out.println("equip gear func");
        int tempRingId = ctx.widgets.component(149,0).itemIds()[0];
        Item ring = ctx.inventory.select().id(tempRingId).poll();
        ring.interact("Wear");
        Condition.sleep(org.powerbot.script.Random.nextInt(100, 150));

    }
}

