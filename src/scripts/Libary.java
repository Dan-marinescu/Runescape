package scripts;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Interactive;
import org.powerbot.script.rt4.World;

import java.awt.*;

import java.util.concurrent.Callable;
public class Libary extends PollingScript<ClientContext>{

    public static final Tile[] pathToBank = {new Tile(3222, 3218, 0), new Tile(3218, 3218, 0), new Tile(3215, 3215, 0), new Tile(3215, 3211, 0), new Tile(3211, 3211, 0), new Tile(3207, 3210, 0), new Tile(3205, 3209, 1), new Tile(3205, 3209, 2), new Tile(3205, 3213, 2), new Tile(3206, 3217, 2), new Tile(3209, 3220, 2)};
    public static final Tile[] pathToWine = {new Tile(3029, 3841, 0), new Tile(3025, 3840, 0), new Tile(3021, 3839, 0), new Tile(3017, 3839, 0), new Tile(3013, 3838, 0), new Tile(3009, 3835, 0), new Tile(3006, 3832, 0), new Tile(3002, 3829, 0), new Tile(2998, 3826, 0), new Tile(2995, 3823, 0), new Tile(2992, 3820, 0), new Tile(2988, 3818, 0), new Tile(2984, 3818, 0), new Tile(2980, 3818, 0), new Tile(2976, 3818, 0), new Tile(2972, 3818, 0), new Tile(2968, 3818, 0), new Tile(2964, 3818, 0), new Tile(2960, 3820, 0), new Tile(2956, 3820, 0), new Tile(2952, 3820, 0)};
    public static final Tile[] pathToLvl30 = {new Tile(2951, 3821, 0), new Tile(2955, 3821, 0), new Tile(2959, 3819, 0), new Tile(2959, 3815, 0), new Tile(2959, 3811, 0), new Tile(2959, 3807, 0), new Tile(2958, 3803, 0), new Tile(2958, 3799, 0), new Tile(2958, 3795, 0), new Tile(2956, 3791, 0), new Tile(2953, 3788, 0), new Tile(2953, 3784, 0), new Tile(2953, 3780, 0), new Tile(2953, 3776, 0), new Tile(2953, 3772, 0), new Tile(2953, 3768, 0), new Tile(2953, 3764, 0), new Tile(2953, 3760, 0), new Tile(2953, 3756, 0)};
    public static final Tile[] pathToWine2 = {new Tile(3027, 3844, 0), new Tile(3025, 3840, 0), new Tile(3021, 3839, 0), new Tile(3017, 3837, 0), new Tile(3014, 3834, 0), new Tile(3010, 3834, 0), new Tile(3006, 3834, 0), new Tile(3002, 3834, 0), new Tile(2998, 3831, 0), new Tile(2994, 3829, 0), new Tile(2990, 3829, 0), new Tile(2986, 3829, 0), new Tile(2982, 3829, 0), new Tile(2978, 3829, 0), new Tile(2974, 3829, 0), new Tile(2970, 3827, 0), new Tile(2967, 3824, 0), new Tile(2963, 3824, 0), new Tile(2959, 3822, 0), new Tile(2955, 3821, 0), new Tile(2951, 3818, 0)};
    final Component magicIcon = ctx.widgets.component(161,58);
    final Component armourIcon = ctx.widgets.component(161,56);
    final Component inventoryIcon = ctx.widgets.component(161,55);
    Component logoutIcon = ctx.widgets.component(161,46);
    final Component teleGrab = ctx.widgets.component(218,24);
    final Component worldSwitcher = ctx.widgets.component(182,7);
    int k =100;

    @Override
    public void poll() {

    }
}
