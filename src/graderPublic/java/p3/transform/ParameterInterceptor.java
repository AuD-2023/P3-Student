package p3.transform;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2_X1;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.DUP_X2;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.POP2;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.SWAP;

/**
 * Class for intercepting parameters of method calls.
 */
public class ParameterInterceptor {

    private final MethodVisitor mv;

    /**
     * Constructs a new {@link ParameterInterceptor} with the supplied {@link MethodVisitor}.
     *
     * @param mv the {@link MethodVisitor}
     */
    public ParameterInterceptor(MethodVisitor mv) {
        this.mv = mv;
    }

    /**
     * Intercept parameters of any method call. <br>
     * This method creates a new {@link Object} array and stores all parameters to the called method in it.
     * Primitive types are boxed and stored as their respective wrapper class.
     * After this method finishes, the array reference is at the very top of the operand stack.
     * The array reference <i>must</i> be removed before invoking the method, e.g. by storing it somewhere.
     * The elements after it are the original parameters in reverse order - as they would be right before the method invocation.
     *
     * @param types the {@link Type} array reflecting the method's parameter types
     */
    public void interceptParameters(Type[] types) {
        mv.visitIntInsn(BIPUSH, types.length);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        for (int i = types.length - 1; i >= 0; i--) {
            if (types[i].getDescriptor().matches("[DJ]")) {
                mv.visitInsn(DUP_X2);
                mv.visitInsn(DUP_X2);
                mv.visitInsn(POP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitInsn(DUP_X2);
                mv.visitInsn(POP);
            } else {
                mv.visitInsn(DUP_X1);
                mv.visitInsn(SWAP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitInsn(SWAP);
            }
            boxPrimitiveValue(types[i]);
            mv.visitInsn(AASTORE);
        }
        for (int i = 0; i < types.length; i++) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            mv.visitInsn(AALOAD);
            unboxPrimitiveValue(types[i]);
            if (types[i].getDescriptor().matches("[DJ]")) {
                mv.visitInsn(DUP2_X1);
                mv.visitInsn(POP2);
            } else {
                mv.visitInsn(SWAP);
            }
        }
    }

    /**
     * Helper method to wrap primitive types in their wrapper class.
     * Reference types are ignored.
     *
     * @param type the type of the parameter
     */
    private void boxPrimitiveValue(Type type) {
        switch (type.getDescriptor()) {
            case "B" -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            case "C" -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            case "D" -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            case "F" -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            case "I" -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            case "J" -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            case "S" -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            case "Z" -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        }
    }

    /**
     * Helper method to cast the parameter to its original type and unbox primitive types, if necessary.
     *
     * @param type the original type of the parameter
     */
    private void unboxPrimitiveValue(Type type) {
        switch (type.getDescriptor()) {
            case "B" -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            }
            case "C" -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            }
            case "D" -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            }
            case "F" -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            }
            case "I" -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            }
            case "J" -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            }
            case "S" -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            }
            case "Z" -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            }
            default -> mv.visitTypeInsn(CHECKCAST, type.getInternalName());
        }
    }

    /**
     * Stores the array reference using the given consumer.
     * This method needs to be called after intercepting the parameters and before invoking the original method.
     * Keep in mind that the array reference always has the type {@link Object}{@code []}.
     *
     * @param consumer the consumer to use
     */
    public void storeArrayRef(Consumer<MethodVisitor> consumer) {
        consumer.accept(mv);
    }

    /**
     * Stores the array reference in the specified field.
     * This method needs to be called after intercepting the parameters and before invoking the original method.
     * Keep in mind that the array reference always has the type {@link Object}{@code []}.
     *
     * @param owner the declaring class of the field
     * @param name  the name of the field
     */
    public void storeArrayRefInField(String owner, String name) {
        storeArrayRef(mv -> mv.visitFieldInsn(PUTSTATIC, owner, name, "[Ljava/lang/Object;"));
    }

    /**
     * Stores the array reference in the specified field.
     * This method needs to be called after intercepting the parameters and before invoking the original method.
     * Keep in mind that the array reference always has the type {@link Object}{@code []}.
     *
     * @param field the field
     */
    public void storeArrayRefInField(Field field) {
        storeArrayRefInField(Type.getInternalName(field.getDeclaringClass()), field.getName());
    }

    /**
     * Adds the array reference to the specified list.
     * This method needs to be called after intercepting the parameters and before invoking the original method.
     * Keep in mind that the array reference always has the type {@link Object}{@code []}.
     *
     * @param owner the declaring class of the field
     * @param name  the name of the field
     */
    public void storeArrayRefInList(String owner, String name) {
        storeArrayRef(mv -> {
            mv.visitFieldInsn(GETSTATIC, owner, name, "Ljava/util/List;");
            mv.visitInsn(SWAP);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            mv.visitInsn(POP);
        });
    }

    /**
     * Add the array reference to the specified list.
     * This method needs to be called after intercepting the parameters and before invoking the original method.
     * Keep in mind that the array reference always has the type {@link Object}{@code []}.
     *
     * @param field the field
     */
    public void storeArrayRefInList(Field field) {
        storeArrayRefInList(Type.getInternalName(field.getDeclaringClass()), field.getName());
    }
}
