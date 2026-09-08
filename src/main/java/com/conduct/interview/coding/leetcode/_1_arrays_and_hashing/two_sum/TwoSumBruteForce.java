package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.two_sum;

// LC 1 — Two Sum
// Задача: дано масив чисел `ar` і число `num`. Знайти ДВА елементи масиву,
// сума яких дорівнює num, і повернути їхні індекси. (Приклад: ar=[2,7,11,15], num=9 -> 2+7=9 -> "0, 1")
//
// Це найпростіший (наївний / брутфорс) спосіб розв'язати задачу:
// перебираємо всі можливі пари елементів по черзі й перевіряємо суму кожної.
// Просто, але повільно — O(n²). Дивись TwoSumHashMap* у цій же папці для швидшого рішення O(n).
// Час O(n²), Пам'ять O(1)
public class TwoSumBruteForce {

    public static String findComplement(int[] ar, int num) {
        for ( int i = 0; i < ar.length; i++ ) {
            for ( int j = i + 1; j < ar.length; j++ ) {
                // j завжди йде після i, щоб не рахувати одну й ту саму пару двічі

                if ( ar[i] + ar[j] == num ) {
                    return i + ", " + j;
                    // знайшли пару, що в сумі дає num -> повертаємо їхні ІНДЕКСИ
                }
            }
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(findComplement(new int[]{3, 5, 2, 1}, 5)); // "0, 2" (3+2=5)
    }
}
