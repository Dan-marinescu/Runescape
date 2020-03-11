package scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Interactive;
import org.powerbot.script.rt4.World;

import javax.swing.*;
import javax.swing.plaf.synth.SynthStyle;
import java.awt.*;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;


@Script.Manifest(
        name = "Alch",
        description = "Alch in wilderness pool",
        properties = "client = 4;"
)


public class Alch extends PollingScript<ClientContext> implements PaintListener{
    final Component logoutIcon = ctx.widgets.component(161,46);
    final Component worldSwitcher = ctx.widgets.component(182,7);
    final Component alchSpell = ctx.widgets.component(218,39);
    final Component magicIcon = ctx.widgets.component(161,58);
    final Component logoutDoorIcon =ctx.widgets.component(69,23);
    Component currentWorldComp = ctx.widgets.component(429,3);

    String input;

    World joinW;

    Random random = new Random();

    int newX;
    int newY;
    int counter =0;
    int min=500000;
    int worldsHopped =0;
    int warningCounter = 0;

    boolean rearrange = true;
    int [] defaultWorlds = {310,350,493,523};
    int i=0;
    Set<String> pkersName = new HashSet<String>();


    public void start(){
        input = JOptionPane.showInputDialog("enter item name:");
    }

    @Override
    public void poll() {

    final State state = getState();
        if (state == null) {
        System.out.println("state is:" + state);
        return;
    }

        switch (state) {
            case HOP:
            System.out.println("State is:" + state);
            System.out.println("pker!!!");
            for(Player p : ctx.players.select().within(50)) {
               if(pkersName.contains(p.name())&&p.name() != ctx.players.local().name())
                    warningCounter++;
               if(p.name() != ctx.players.local().name())
                   pkersName.add(p.name());
            }
            logoutIcon.click();
            if (ctx.widgets.component(182, 7).valid())
                worldSwitcher.click();
            ctx.input.send("" + 2);
            joinW.hop();
            worldsHopped++;
            rearrange = true;
            break;

            case REARRANGE:
            System.out.println("state is:" + state);
            int myX = ctx.players.local().tile().x();
            int myY = ctx.players.local().tile().y();
            newX = myX;
            newY = myY;
            while(newX == myX && newY == myY) {
                newX = random.nextInt(16) + 3369;
                newY = random.nextInt(10) + 3890;
            }
            final Tile newT = new Tile(newX,newY,0);
            ctx.movement.step(newT);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.movement.distance(newT) == 1 || ctx.players.select().within(50).size() > 1;
                }
            },50,100);



            if(warningCounter%2 == 1) {
                System.out.println("warning..." + joinW.id());
                ctx.worlds.select().id(defaultWorlds[i % (defaultWorlds.length - 1)]).joinable().peek().hop();
                i++;
                System.out.println("warning2..." + joinW.id());
            }
            currentWorldComp = ctx.widgets.component(429, 3);
            String worldString = currentWorldComp.text().replaceAll("[\\D]", "");
            int maxW1, minW2;
            if (worldString != "") {
                maxW1 = Integer.parseInt(currentWorldComp.text().replaceAll("[\\D]", "")) + 10;
                minW2 = Integer.parseInt(currentWorldComp.text().replaceAll("[\\D]", "")) - 10;

            } else {
                maxW1 = 525;
                minW2 = 491;

            }
            final int maxW = maxW1;
            final int minW = minW2;
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
            rearrange = false;
            System.out.println("next World is:"+ joinW.id());
            break;

            case ALCH:
                System.out.println("state is:"+ state);
                if(!ctx.magic.casting(Magic.Spell.HIGH_ALCHEMY))
                    magicIcon.click();
                Item ring = ctx.inventory.select().name(input).poll();
                ctx.magic.cast(Magic.Spell.HIGH_ALCHEMY);
                ring.interact("Cast");
                if(min > ctx.inventory.select().name(input).count(true)) {
                    counter++;
                    min = ctx.inventory.select().name(input).count(true);
                }
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() != 713 || ctx.players.select().within(50).size() > 1;
                    }
                },50,100);
                break;

            case FINISH:
                System.out.println("state is:"+ state);
                logoutIcon.click();
                if (ctx.widgets.component(182, 7).valid())
                    worldSwitcher.click();
                logoutDoorIcon.click();
                break;

          }
    }
    private State getState () {
        if (ctx.inventory.select().name(input).size()==0)
            return State.FINISH;
        if(ctx.players.select().within(50).size() > 1)
            return State.HOP;
        else if (rearrange)
            return State.REARRANGE;
        else
            return State.ALCH;

    }

    private enum State {
        HOP, ALCH,FINISH,REARRANGE
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

        g.drawString("wildy alcher by Kaskas",20,20);
        g.drawString("running: "+String.format("%02d:%02d:%02d",hours,minutes,seconds),20,40);
        g.drawString("alchs P/H:"+ (int)(counter*(3600000D/milliseconds)),20,60);
        g.drawString("alched collected:"+ counter,20,80);
        g.drawString("worlds hopped: "+ worldsHopped,20,100);
        g.drawString("current state:"+getState(),20,120);
        g.drawString("pkers:"+ pkersName,20,140);
    }
}



