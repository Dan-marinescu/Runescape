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
        name = "Looter",
        description = "looting at GE",
        properties = "client = 4;"
)
public class Looter extends PollingScript<ClientContext> implements PaintListener{
    final Component armourIcon = ctx.widgets.component(161,56);
    final Component inventoryIcon = ctx.widgets.component(161,55);

    Random random = new Random();



    final Tile geTile = new Tile(3173,3481,0);
    final Tile safeTileS = new Tile(3178,3481,0);
    final Tile safeTileN = new Tile(3175,3499,0);
    final Tile safeTileW = new Tile(3154,3481,0);

    final Tile GE = new Tile(3163,3477,0);
    final Tile lumb = new Tile(3221,3217,0);

    final int RING_5 = 11980;
    final int RING_4 = 11982;
    final int RING_3 = 11984;
    final int RING_2 = 11986;
    final int RING_1 = 11988;

    int itemsLooted =0;
    int locationIndex =0;
    int randomRun =10;
    int randomTime = 240;

    final int[] junk = {229,526,1061};

    final String [] locations = {"North east", "South east", "south west"};

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
            case SCAN:
                System.out.println("State is:"+ state);
                if(getTime()%randomTime == 0 ) {
                    randomTime = random.nextInt(30)+240;
                    ctx.input.send(Integer.toString(randomTime));
                }
                if(ctx.inventory.select().id(junk).count()>0)
                    ctx.inventory.select().id(junk).poll().interact(true,"Drop");
                for(Player p: ctx.players.select().within(50)){
                    if((p.healthPercent() == 0 || p.animation() == 836) && !nameQ.contains(p.name()) && p.appearance()[3] != -1) {

                        tq.add(p.tile());
                        timeQ.add(getTime());
                        nameQ.add(p.name());
                    }

                }

                break;

            case LOOT:
                System.out.println("State is:"+ state);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        ctx.movement.step(tq.peek());
                        return ctx.movement.distance(tq.peek()) == 1 || atArea(lumb);
                    }
                },400,12);
                if(nameQ.peek() == ctx.players.local().name()){
                    nameQ.poll();
                    timeQ.poll();
                    tq.poll();
                }
                while(!timeQ.isEmpty() && timeQ.peek() + 50 < getTime() && timeQ.peek() + 65 > getTime() || ctx.groundItems.select().within(0).poll().id() != -1 && !atArea(lumb)) {
                    for(Player p: ctx.players.select().within(50)){
                        if((p.healthPercent() == 0 || p.animation() == 836) && !nameQ.contains(p.name()) && p.appearance()[3] != -1) {

                            tq.add(p.tile());
                            timeQ.add(getTime());
                            nameQ.add(p.name());
                        }

                    }
                    ctx.groundItems.select().within(0).poll().click();
                    if (ctx.inventory.select().count()==28)
                        break;
                }
                tq.poll();
                timeQ.poll();
                nameQ.poll();
                ctx.movement.step(safeTileS);
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
                    itemsLooted += ctx.inventory.select().count();
                    ctx.bank.depositInventory();
                    ctx.bank.close();
                }
                ctx.movement.step(safeTileS);
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
                    ctx.bank.depositInventory();
                    if(gotRing()) {
                        getRing();
                        ctx.bank.close();
                        equipGear();
                        armourIcon.click();
                        Component ringComp = ctx.widgets.component(387,23).component(1);
                        ringComp.interact(true,"Grand Exchange");
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return atArea(GE);
                            }
                        },500,5);
                        ctx.movement.step(safeTileS);
                        inventoryIcon.click();
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
        if (!ctx.movement.running() && ctx.movement.energyLevel() > randomRun) {
            ctx.movement.running(true);
            randomRun = random.nextInt(5)+8;
        }
        if(ctx.players.local().animation() == 424 && !atArea(lumb)) {
            System.out.println("im under attack!");
            ctx.movement.step(safeTileS);
        }
        if(!nameQ.isEmpty())
            cleanQs();
        if (atArea(lumb)) {
            while(timeQ.size()>0)
                cleanQs();
            return State.RETURNGE;
        }
        if(timeQ.size() == 0 && ctx.inventory.select().count() > 5)
            return State.BANK;
        else if(!timeQ.isEmpty() && timeQ.peek()+50< getTime() && ctx.inventory.select().count()!= 28)
            return State.LOOT;
        else if(ctx.inventory.select().count()>14)
            return State.BANK;
        else
            return State.SCAN;


    }
    private enum State {
        LOOT,SCAN,BANK,RETURNGE
    }


    public long getTime(){
        return (this.getTotalRuntime() / 1000);
    }
    public void cleanQs(){
        if(timeQ.size() >0 && timeQ.peek()+75 < getTime() && ctx.groundItems.select().within(0).poll().id() != -1){
            timeQ.poll();
            tq.poll();
            nameQ.poll();
        }
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
        g.drawString("Queue size:"+tq.size(),20,20);
        g.drawString("state is:"+getState(),20,40);
        g.drawString("running: "+seconds,20,60);
        g.drawString("items banked: "+itemsLooted,20,80);
        g.drawString("current location: "+itemsLooted,20,100);

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

