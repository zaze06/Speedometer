package me.alien.speedometer.events;

import me.alien.speedometer.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Events {

    static ArrayList<Double> speeds = new ArrayList<>();
    static int combo = 0;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void Speed(RenderGameOverlayEvent event){
        if(!event.isCanceled() && event.getType() == RenderGameOverlayEvent.ElementType.ALL){
            //int posX = (event.getWindow().getGuiScaledWidth()) / 2;
            //int posY = (event.getWindow().getGuiScaledHeight()) / 2;
            Entity entity = Minecraft.getInstance().player.getRootVehicle();
            /*if(entity.getVehicle() != null){
                entity = entity.getVehicle();
            }*/
            Level world = entity.level;
            double x = entity.position().x;
            double y = entity.position().y;
            double z = entity.position().z;

            Vec3 vec = entity.getDeltaMovement();
            Color color = new ColorUIResource(Integer.parseInt(Config.Client.color.get(), 16));

            if(Config.Client.enabled.get()) {
                double yOffset = Config.Client.yOff.get();
                double xOffset = Config.Client.xOff.get();
                double zOffset = Config.Client.zOff.get();
                double vOffset = Config.Client.vOff.get();

                if (entity instanceof Player e) {
                    if (!e.isOnGround() && e.isCreative()) {
                        yOffset = 0;
                    } else if (e.isInWater()) {
                        yOffset = 0;
                    }
                } else if (entity instanceof Boat) {
                    yOffset = 0;
                }

                double speed = (Math.sqrt(Math.pow(vec.x + xOffset, 2) + Math.pow(vec.y + yOffset, 2) + Math.pow(vec.z + zOffset, 2)) * 20)+vOffset;
                //Minecraft.getInstance().
                if (speeds.size() >= Config.Client.maxSampleSize.get()) {
                    speeds.remove(0);
                }
                speeds.add(speed);
                speed = 0;
                for (Double aDouble : speeds) {
                    speed += aDouble;
                }
                speed = speed / speeds.size();

                String speedType = "";
                if (entity instanceof Boat && Config.Client.useKnotInBoat.get()) {
                    speed = speed * 1.94384449;
                    speedType = "knot";
                } else {
                    speedType = Config.Client.speedType.get().toString();
                    if (speedType.equals("km/h")) {
                        speed = speed * 3.6;
                    } else if (speedType.equals("mph")) {
                        speed = speed * 2.23693629;
                    } else {
                        speedType = "m/s";
                    }
                }


                if (true) {

                    String format = String.format("%.2f", speed);
                    //Minecraft.getInstance().fo
                    Minecraft.getInstance().font.draw(
                            event.getMatrixStack(),
                            format + " " + speedType,
                            getPos(event, (String) Config.Client.xPos.get()/*"W-70"*/, 0, false),
                            getPos(event, (String) Config.Client.yPos.get()/*"Y-15""H-15"*/, 1, true),
                            color.getRGB());
                }
            }
            /*if(Config.combat.isEnabled.get()) {
                if(Config.combat.combo.isEnabled.get()){
                    Minecraft.getInstance().font.draw(
                            event.getMatrixStack(),
                            combo+"",
                            getPos(event, Config.speedometer.xPos.get()/*"W-70"*//*, 0, false),
                            getPos(event, Config.speedometer.yPos.get()/*"Y-15""H-15"*//*, 1, true),
                            color.getRGB());
                }
            }*/

        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onHit(AttackEntityEvent e){
        if(e.getTarget() instanceof LivingEntity){
            if(e.getPlayer() == Minecraft.getInstance().player){
                combo++;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onDamage(LivingDamageEvent e){
        if(e.getEntity() instanceof Player){
            if(e.getEntity() == Minecraft.getInstance().player){
                combo = 0;
            }
        }
    }



    static boolean flag = true;

    private static int getPos(RenderGameOverlayEvent event, String input, int type, boolean changeFlag) {
        ArrayList<String> paserdPos = new ArrayList<String>();
        final char[] s = input.toCharArray();
        try{
            for(int i = 0; i <s.length; i++){
                if(s[i] == 'W' || s[i] == 'H'){
                    if(type == 0) paserdPos.add(event.getWindow().getGuiScaledWidth()+"");
                    else if(type == 1) paserdPos.add(event.getWindow().getGuiScaledHeight()+"");
                }else if(s[i] == 'h' || s[i] == 'w'){
                    if(type == 0) paserdPos.add(((int)(event.getWindow().getGuiScaledWidth()/2))+"");
                    else if(type == 1) paserdPos.add(((int)(event.getWindow().getGuiScaledHeight()/2))+"");
                }else if(s[i] == '+'){
                    paserdPos.add("+");
                }else if(s[i] == '-'){
                    paserdPos.add("-");
                }else if(s[i] == '*'){
                    paserdPos.add("/");
                }else if(s[i] == '/'){
                    paserdPos.add("/");
                }else if(testIfInt(s[i])){
                    try{
                        Integer.parseInt(paserdPos.get(i-1));
                        paserdPos.add(i-1,paserdPos.get(i-1)+s[i]);
                    }catch (NumberFormatException e){
                        paserdPos.add(Character.toString(s[i]));
                    }
                }else{
                    throw new Exception();
                }
            }
        }catch (Exception e){
            paserdPos.clear();
            if(type == 0){
                paserdPos.add(event.getWindow().getGuiScaledWidth()+"");
                paserdPos.add("-");
                paserdPos.add("70");
            }else if(type == 1){
                paserdPos.add(event.getWindow().getGuiScaledHeight()+"");
                paserdPos.add("-");
                paserdPos.add("15");
            }
        }



        int xPos = 0;
        try{
            xPos = Integer.parseInt(paserdPos.get(0));
        }catch (NumberFormatException e){
            if(type == 0){
                paserdPos.add(event.getWindow().getGuiScaledWidth()+"");
                paserdPos.add("-");
                paserdPos.add("70");
            }else if(type == 1){
                paserdPos.add(event.getWindow().getGuiScaledHeight()+"");
                paserdPos.add("-");
                paserdPos.add("15");
            }
            xPos = Integer.parseInt(paserdPos.get(0));
        }

        for(int i = 1; i < paserdPos.size(); i++){
            boolean first = i == 0;
            String s1 = paserdPos.get(i);
            String s2 = "";
            try{
                s2 = paserdPos.get(i+1);
            }catch (Exception e){
                first = true;
            }

            if(s1 == "+" && !first){
                xPos += Integer.parseInt(s2);
            }else if(s1 == "-" && !first){
                xPos -= Integer.parseInt(s2);
            }else if(s1 == "*" && !first){
                xPos *= Integer.parseInt(s2);
            }else if(s1 == "/" && !first){
                xPos /= Integer.parseInt(s2);
            }
        }
        if(flag) {
            System.out.println("Selected speed type: "+Config.Client.speedType.get());
            System.out.println(Arrays.toString(paserdPos.toArray()));
            System.out.println(xPos+"");
            flag = !changeFlag;
        }
        return xPos;
    }

    private static boolean testIfInt(char c) {
        int i = Integer.parseInt(Character.toString(c));
        return (i == 0 || i == 1 || i == 2 ||
                i == 3 || i == 4 || i == 5 ||
                i == 6 || i == 7 || i == 8 ||
                i == 9);
    }
}
