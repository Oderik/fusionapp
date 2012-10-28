package de.oderik.fusionlwp.util;

/**
 * @author Maik.Riechel
 * @since 28.10.12
 */
public class Tag {
  public static String of(Class<?> aClass) {
    return "FusionFestival_" + aClass.getSimpleName();
  }
}
