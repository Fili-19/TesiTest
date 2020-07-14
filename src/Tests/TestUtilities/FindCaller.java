package Tests.TestUtilities;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;


public class FindCaller {

    private static HashMap<String, HashMap<String, InvocationInfo>> methodInvocations;
    private static String implementationMethodName;
    private static OperatingMode runningMode;
    private static String dataFile;

    static {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "config.properties";
        Properties appProps = new Properties();

        try {
            appProps.load(new FileInputStream(appConfigPath));
            OperatingMode operatingMode = OperatingMode.valueOf(appProps.getProperty("OperatingMode"));
            switch (operatingMode) {
                case Record:
                    runningMode = OperatingMode.Record;
                    break;
                case Evaluate:
                    runningMode = OperatingMode.Evaluate;
                    break;
                default:
                    runningMode = OperatingMode.None;
            }
            String outputDirectory =  appProps.getProperty("FindCallerOutput");
            dataFile = outputDirectory + "FindCallerDataFile";

            switch (runningMode) {
                case Record:
                    try {
                        loadDataFile();
                    }
                    catch (Exception e) {
                        methodInvocations = new HashMap<>();
                    }
                    //TODO
                    //saveDataFile();
                    break;
                case Evaluate:
                    loadDataFile();
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void saveDataFile() {
        try {
            FileOutputStream fileOut = new FileOutputStream(dataFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(methodInvocations);
            objectOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadDataFile() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(dataFile);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        methodInvocations = (HashMap<String, HashMap<String, InvocationInfo>>) objectIn.readObject();
        objectIn.close();
    }

    public static String getTestName() {
        if (runningMode == OperatingMode.None)
            return null;

        InvocationInfo currentInvocationInfo = extractStackInformation();
        if (currentInvocationInfo == null)
            return null;

        switch (runningMode) {
            case Record:
                if (!methodInvocations.containsKey(implementationMethodName))
                    methodInvocations.put(implementationMethodName, new HashMap<>());
                methodInvocations.get(implementationMethodName).put(currentInvocationInfo.getMethodName(), currentInvocationInfo);
                //todo to be removed
                saveDataFile();
                return null;
            case Evaluate:
                HashMap<String, InvocationInfo> implementationInvokers = methodInvocations.get(implementationMethodName);
                if (implementationInvokers == null)
                    return null;
                InvocationInfo savedInfo = implementationInvokers.get(currentInvocationInfo.getMethodName());
                if (savedInfo == null)
                    return null;
                if (savedInfo.getLineNumber() == currentInvocationInfo.getLineNumber()) {
                    //TODO
                    assert savedInfo.getSourceFileName().equals(currentInvocationInfo.getSourceFileName());
                    assert savedInfo.getClassName().equals(currentInvocationInfo.getClassName());
                    assert savedInfo.getMethodName().equals(currentInvocationInfo.getMethodName());
                    return currentInvocationInfo.getMethodName();
                }
                return null;
            default:
                throw new IllegalStateException();
        }
    }

    private static InvocationInfo extractStackInformation() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        assert stackTrace.length > 4;
        assert stackTrace[0].getMethodName().equals("getStackTrace");
        assert stackTrace[1].getMethodName().equals("extractStackInformation");
        assert stackTrace[2].getMethodName().equals("getTestName");

        implementationMethodName = stackTrace[3].getMethodName();

        try {
            Class<?> directCallerClass = Class.forName(stackTrace[3].getClassName());
            String directCallerPackage = directCallerClass.getPackageName();

            for(int i = 4; i < stackTrace.length; ++i) {
                Class<?> indirectCallerClass = Class.forName(stackTrace[i].getClassName());
                String indirectCallerPackage = indirectCallerClass.getPackageName();
                if (directCallerPackage.equals(indirectCallerPackage))
                    return null;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for(int i = stackTrace.length - 1; i >= 0; --i) {
            StackTraceElement frame = stackTrace[i];
            String methodName = frame.getMethodName();
            String className = frame.getClassName();

            try {
                Class<?> c = Class.forName(className);
                Method method = c.getDeclaredMethod(methodName);
                Annotation[] annotations = method.getDeclaredAnnotations();

                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getTypeName().equals("org.junit.jupiter.api.Test")) {
                        return new InvocationInfo(frame.getLineNumber(), frame.getFileName(), methodName, frame.getClassName());
                    }
                }
            } catch(NoSuchMethodException ignored) {

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException();
    }
}

class InvocationInfo implements Serializable {

    private int lineNumber;
    private String sourceFileName;
    private String methodName;
    private String className;

    public InvocationInfo(int lineNumber, String sourceFileName, String methodName, String className) {
        this.lineNumber = lineNumber;
        this.sourceFileName = sourceFileName;
        this.methodName = methodName;
        this.className = className;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }
}
