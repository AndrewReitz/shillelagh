package shillelagh.internal;

/** Writes code for shillelagh injected code to utilize */
public class ShillelaghUtilWriter {

  static final String SERIALIZE_FUNCTION = "serialize";
  static final String DESERIALIZE_FUNCTION = "deserialize";

  /** Creating internal shillelagh code */
  String brewInternalJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code from Shillelagh. Do not modify!\n");
    builder.append("package shillelagh;\n\n");
    builder.append("import java.io.ByteArrayInputStream;\n");
    builder.append("import java.io.ByteArrayOutputStream;\n");
    builder.append("import java.io.IOException;\n");
    builder.append("import java.io.ObjectInputStream;\n");
    builder.append("import java.io.ObjectOutputStream;\n\n");
    builder.append("public final class Util {\n");
    emmitByteArraySerialization(builder);
    builder.append("}\n");
    return builder.toString();
  }

  /** Creates functions for serialization to and from byte arrays */
  private void emmitByteArraySerialization(StringBuilder builder) {
    builder.append("  public static <K> byte[] ").append(SERIALIZE_FUNCTION).append("(K object) {\n");
    builder.append("    try {\n");
    builder.append("      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();\n");
    builder.append("      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);\n");
    builder.append("      objectOutputStream.writeObject(object);\n");
    builder.append("      return byteArrayOutputStream.toByteArray();\n");
    builder.append("    } catch (IOException e) {\n");
    builder.append("      throw new RuntimeException(e);\n");
    builder.append("    }\n");
    builder.append("  }\n\n");
    builder.append("  public static <K> K ").append(DESERIALIZE_FUNCTION).append("(byte[] bytes) {\n");
    builder.append("    try {\n");
    builder.append("      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);\n");
    builder.append("      ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);\n");
    builder.append("      return (K) objectInputStream.readObject();\n");
    builder.append("    } catch (IOException e) {\n");
    builder.append("      throw new RuntimeException(e);\n");
    builder.append("    } catch (ClassNotFoundException e) {\n");
    builder.append("      throw new RuntimeException(e);\n");
    builder.append("    }\n");
    builder.append("  }\n");
  }
}
