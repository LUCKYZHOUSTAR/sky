package lucky.sky.util.convert;

import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 集合转换器。
 */
public class ListConverter {

  public static int[] toInt32Array(Collection<Integer> list) {
    if (list == null) {
      return new int[0];
    }
    int[] rez = new int[list.size()];
    int i = 0;
    for (Integer n : list) {
      rez[i++] = n;
    }
    return rez;
  }

  public static int[] toInt32Array(List<Integer> list) {
    if (list == null) {
      return new int[0];
    }
    int[] rez = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      rez[i] = list.get(i);
    }
    return rez;
  }

  public static List<Integer> toInt32List(int[] array) {
    if (array == null) {
      return Collections.emptyList();
    }
    List<Integer> rez = new ArrayList<>();
    for (val i : array) {
      rez.add(i);
    }
    return rez;
  }
}
