-assumenosideeffects class android.util.Log {
    public static int i(...);
    public static int d(...);
}
# 난독화 시 crashlystics에서 판독 가능한 비정상 종료 보고서 생성에 필요한 정보 보존
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.