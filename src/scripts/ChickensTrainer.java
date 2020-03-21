package scripts;

import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import javax.swing.*;

public class FreeToPlayFighter extends PollingScript<ClientContext> {
    String monsterNameInput;

    boolean buryFlag;
    boolean cookFlag;

    public void start(){
        monsterNameInput = JOptionPane.showInputDialog("enter monster name (key sensitive):");
        buryFlag = (JOptionPane.showInputDialog("bury bones? (Y/N)")=="Y" ? true : false);
        cookFlag = (JOptionPane.showInputDialog("cook? (Y/N)")=="Y" ? true : false);

    }



    @Override
    public void poll() {
        final State state = getState();
        if(state ==null) {
            return;
        }

    }

    private State getState() {

        if (ctx.inventory.isFull()&&cookFlag)
            return State.COOK;
        else if(loot.valid())
            return State.LOOT;
        else if(monster.valid()&&shouldAttack()&&gotPrayer())
            return State.FIGHT;
        else
            return null;
    }

    private enum State{
        LOOT,FIGHT,COOK
    }
}
