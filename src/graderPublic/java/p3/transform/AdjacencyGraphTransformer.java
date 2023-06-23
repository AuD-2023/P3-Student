package p3.transform;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.sourcegrade.jagr.api.testing.ClassTransformer;

public class AdjacencyGraphTransformer implements ClassTransformer {

    @Override
    public String getName() {
        return "AdjacencyGraphTransformer";
    }

    @Override
    public int getWriterFlags() {
        return ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
    }

    @Override
    public void transform(ClassReader reader, ClassWriter writer) {
        if (reader.getClassName().equals("p3/graph/AdjacencyGraph")) {
            reader.accept(new Transformer(writer), ClassReader.SKIP_DEBUG);
        } else {
            reader.accept(writer, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
    }

    private static class Transformer extends ClassVisitor {

        protected Transformer(ClassVisitor classVisitor) {
            super(Opcodes.ASM9, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (name.equals("<init>") && descriptor.equals("(Ljava/util/Set;Ljava/util/Set;)V")) {
                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        if (opcode == Opcodes.INVOKEVIRTUAL &&
                            owner.equals("p3/graph/AdjacencyMatrix") &&
                            name.equals("addEdge") &&
                            descriptor.equals("(III)V")) {
                            ParameterInterceptor parameterInterceptor = new ParameterInterceptor(this);
                            parameterInterceptor.interceptParameters(new Type[]{Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE});
                            parameterInterceptor.storeArrayRefInList("p3/graph/AdjacencyGraphTests", "addEdgeParameters");
                        }
                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    }
                };
            } else {
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        }
    }
}
