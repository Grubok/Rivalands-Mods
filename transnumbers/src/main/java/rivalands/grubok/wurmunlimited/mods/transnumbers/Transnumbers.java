package rivalands.grubok.wurmunlimited.mods.transnumbers;

import javassist.*;
import javassist.bytecode.*;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modloader.classhooks.*;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by me on 9/11/2016.
 */
public class Transnumbers implements WurmServerMod, Configurable,PreInitable, Initable {


    private Logger logger = Logger.getLogger(this.getClass().getName());
    private int transNumber = 100;


    @Override
    public void configure(Properties properties) {
        transNumber = Integer.valueOf(properties.getProperty("transNumber", Integer.toString(transNumber)));
        logger.log(Level.INFO, "Points to Transmute a Tile: " + transNumber);

    }

    @Override
    public void preInit() {
        if (transNumber != 100) {
            initTransNumber();
        }
    }

    private void initTransNumber() {
        try {

            ClassPool classPool = HookManager.getInstance().getClassPool();
            CtClass tileBehaviour = classPool.get("com.wurmonline.server.behaviours.TileBehaviour");
            // private boolean handle_TRANSMUTATE(Creature performer, Item source, int tilex, int tiley, int tile, Action act, float counter)

            CtClass[] paramTypes = {
                    classPool.get("com.wurmonline.server.creatures.Creature"),
                    classPool.get("com.wurmonline.server.items.Item"),
                    CtPrimitiveType.intType,
                    CtPrimitiveType.intType,
                    CtPrimitiveType.intType,
                    classPool.get("com.wurmonline.server.behaviours.Action"),
                    CtPrimitiveType.floatType
            };
            CtMethod method = tileBehaviour.getMethod("handle_TRANSMUTATE", Descriptor.ofMethod(CtPrimitiveType.booleanType, paramTypes));
            MethodInfo methodInfo = method.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

            LocalNameLookup localNames = new LocalNameLookup((LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag));
            // look for if(newTileQLCount >= 100)
            Bytecode bytecode = new Bytecode(methodInfo.getConstPool());
            bytecode.addIload(localNames.get("newTileQLCount"));
            bytecode.add(Bytecode.BIPUSH, 100);
            byte[] search = bytecode.get();


            bytecode = new Bytecode(methodInfo.getConstPool());
            // changing to (newTileQLCount >= (our number))
            bytecode.addIload(localNames.get("newTileQLCount"));
            bytecode.add(Bytecode.BIPUSH, transNumber);
            byte[] replacement = bytecode.get();

            new CodeReplacer(codeAttribute).replaceCode(search, replacement);
            methodInfo.rebuildStackMap(classPool);

        } catch (NotFoundException | BadBytecode e) {
            throw new HookException(e);
        }
    }

    private void initTransReturn(){
        // this is to return apropriate transmuted message per change

    }

    @Override
    public void init() {

        logger.log(Level.INFO, "Adjusting tile examines");
        if (transNumber != 100) {
            try {

                ClassPool classPool = HookManager.getInstance().getClassPool();
                CtClass tileBehaviour = classPool.get("com.wurmonline.server.behaviours.TileBehaviour");


                //static void sendTileTransformationState(Creature performer, int tilex, int tiley, byte tileType)
                CtClass[] paramTypes = {
                        classPool.get("com.wurmonline.server.creatures.Creature"),
                        CtPrimitiveType.intType,
                        CtPrimitiveType.intType,
                        CtPrimitiveType.byteType
                };

                CtMethod method = tileBehaviour.getMethod("sendTileTransformationState", Descriptor.ofMethod(CtPrimitiveType.voidType, paramTypes));
                MethodInfo methodInfo = method.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

                LocalNameLookup localNames = new LocalNameLookup((LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag));
                /*
                 if(potionCount > 97) {
                    performer.getCommunicator().sendNormalServerMessage("The tile is so close to being completely transformed.");
                } else if(potionCount > 90) {
                    performer.getCommunicator().sendNormalServerMessage("The tile has almost been completely transformed.");
                } else if(potionCount > 75) {
                    performer.getCommunicator().sendNormalServerMessage("The tile is over three quarters transformed.");
                } else if(potionCount > 50) {
                    performer.getCommunicator().sendNormalServerMessage("The tile is over half way transformed.");
                } else if(potionCount > 25) {
                    performer.getCommunicator().sendNormalServerMessage("The tile is over a quarter transformed.");
                } else if(potionCount > 0) {
                    performer.getCommunicator().sendNormalServerMessage("Someone has started transforming this tile.");
                }
                 */
                Bytecode bytecode = new Bytecode(methodInfo.getConstPool());
                bytecode.addIload(localNames.get("potionCount"));
                bytecode.add(Bytecode.BIPUSH, 97);
                byte[] search = bytecode.get();

                bytecode = new Bytecode(methodInfo.getConstPool());
                // changing (potionCount > (ournumber/97))
                bytecode.addIload(localNames.get("potionCount"));
                bytecode.add(Bytecode.BIPUSH, ((transNumber*97)/100));
                byte[] replacement = bytecode.get();
                new CodeReplacer(codeAttribute).replaceCode(search, replacement);

                //finding (potionCount > 90)----------------
                Bytecode bytecode1 = new Bytecode(methodInfo.getConstPool());
                bytecode1.addIload(localNames.get("potionCount"));
                bytecode1.add(Bytecode.BIPUSH, 90);
                byte[] search1 = bytecode1.get();

                bytecode1 = new Bytecode(methodInfo.getConstPool());
                // changing (potionCount > (ournumber/90))
                bytecode1.addIload(localNames.get("potionCount"));
                bytecode1.add(Bytecode.BIPUSH, ((transNumber*90)/100));
                byte[] replacement1 = bytecode1.get();
                new CodeReplacer(codeAttribute).replaceCode(search1, replacement1);

                //finding (potionCount > 75)----------------
                Bytecode bytecode2 = new Bytecode(methodInfo.getConstPool());
                bytecode2.addIload(localNames.get("potionCount"));
                bytecode2.add(Bytecode.BIPUSH, 75);
                byte[] search2 = bytecode2.get();
                bytecode2 = new Bytecode(methodInfo.getConstPool());
                // changing (potionCount > (ournumber/75))
                bytecode2.addIload(localNames.get("potionCount"));
                bytecode2.add(Bytecode.BIPUSH, ((transNumber*75)/100));
                byte[] replacement2 = bytecode2.get();
                new CodeReplacer(codeAttribute).replaceCode(search2, replacement2);

                //finding (potionCount > 50)-------------------
                Bytecode bytecode3 = new Bytecode(methodInfo.getConstPool());
                bytecode3.addIload(localNames.get("potionCount"));
                bytecode3.add(Bytecode.BIPUSH, 50);
                byte[] search3 = bytecode3.get();
                if (transNumber == 50) {
                    bytecode3 = new Bytecode(methodInfo.getConstPool());
                    // changing (potionCount > (ournumber/50))
                    bytecode3.addIload(localNames.get("potionCount"));
                    bytecode3.add(Bytecode.BIPUSH, ((transNumber * 50) / 100) - 1);
                    byte[] replacement3 = bytecode3.get();
                    new CodeReplacer(codeAttribute).replaceCode(search3, replacement3);
                } else{
                    bytecode3 = new Bytecode(methodInfo.getConstPool());
                    // changing (potionCount > (ournumber/50))
                    bytecode3.addIload(localNames.get("potionCount"));
                    bytecode3.add(Bytecode.BIPUSH, ((transNumber * 50) / 100));
                    byte[] replacement3 = bytecode3.get();
                    new CodeReplacer(codeAttribute).replaceCode(search3, replacement3);
                }
                //finding (potionCount > 25)-------------------
                Bytecode bytecode4 = new Bytecode(methodInfo.getConstPool());
                bytecode4.addIload(localNames.get("potionCount"));
                bytecode4.add(Bytecode.BIPUSH, 25);
                byte[] search4 = bytecode4.get();

                bytecode4 = new Bytecode(methodInfo.getConstPool());
                // changing (potionCount > (ournumber/25))
                bytecode4.addIload(localNames.get("potionCount"));
                bytecode4.add(Bytecode.BIPUSH, ((transNumber*25)/100));
                byte[] replacement4 = bytecode4.get();
                new CodeReplacer(codeAttribute).replaceCode(search4, replacement4);
                methodInfo.rebuildStackMap(classPool);

            } catch (NotFoundException | BadBytecode e) {
                throw new HookException(e);
            }

        }
    }
}


