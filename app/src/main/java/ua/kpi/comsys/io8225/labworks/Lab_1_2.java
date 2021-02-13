package ua.kpi.comsys.io8225.labworks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Lab_1_2 {
    public static void main(String[] args) {
        // Частина 1

        // Дано рядок у форматі "Student1 - Group1; Student2 - Group2; ..."

        String studentsStr = "Дмитренко Олександр - ІП-84; Матвійчук Андрій - ІВ-83; Лесик Сергій - ІО-82; Ткаченко Ярослав - ІВ-83; Аверкова Анастасія - ІО-83; Соловйов Даніїл - ІО-83; Рахуба Вероніка - ІО-81; Кочерук Давид - ІВ-83; Лихацька Юлія - ІВ-82; Головенець Руслан - ІВ-83; Ющенко Андрій - ІО-82; Мінченко Володимир - ІП-83; Мартинюк Назар - ІО-82; Базова Лідія - ІВ-81; Снігурець Олег - ІВ-81; Роман Олександр - ІО-82; Дудка Максим - ІО-81; Кулініч Віталій - ІВ-81; Жуков Михайло - ІП-83; Грабко Михайло - ІВ-81; Іванов Володимир - ІО-81; Востриков Нікіта - ІО-82; Бондаренко Максим - ІВ-83; Скрипченко Володимир - ІВ-82; Кобук Назар - ІО-81; Дровнін Павло - ІВ-83; Тарасенко Юлія - ІО-82; Дрозд Світлана - ІВ-81; Фещенко Кирил - ІО-82; Крамар Віктор - ІО-83; Іванов Дмитро - ІВ-82";

        // Завдання 1
        // Заповніть словник, де:
        // - ключ – назва групи
        // - значення – відсортований масив студентів, які відносяться до відповідної групи

        HashMap<String, ArrayList<String>> studentsGroups = new HashMap<>();

        // Ваш код починається тут

        String[] studentsWithGroup = studentsStr.split("; ");

        for (int i=0; i<studentsWithGroup.length; i++) {
            String[] studentGroup = studentsWithGroup[i].split(" - ");

            if (studentsGroups.containsKey(studentGroup[1]) == false)
                studentsGroups.put(studentGroup[1], new ArrayList<>());

            studentsGroups.get(studentGroup[1]).add(studentGroup[0]);
        }

        String[] groupsKeys = studentsGroups.keySet().toArray(new String[studentsGroups.size()]);
        for (int i=0;i<groupsKeys.length;i++) {
            Collections.sort(studentsGroups.get(groupsKeys[i]));
        }

        // Ваш код закінчується тут

        System.out.println("Завдання 1");
        System.out.println(studentsGroups.entrySet());

        // Дано масив з максимально можливими оцінками

        int[] points = {12, 12, 12, 12, 12, 12, 12, 16};

        // Завдання 2
        // Заповніть словник, де:
        // - ключ – назва групи
        // - значення – словник, де:
        //   - ключ – студент, який відносяться до відповідної групи
        //   - значення – масив з оцінками студента (заповніть масив випадковими значеннями, використовуючи функцію `randomValue(maxValue: Int) -> Int`)

        HashMap<String, HashMap<String, ArrayList<Integer>>> studentPoints = new HashMap<>();

        // Ваш код починається тут

        for (int i=0;i<groupsKeys.length;i++) {

            if (studentPoints.containsKey(groupsKeys[i]) == false)
                studentPoints.put(groupsKeys[i], new HashMap<>());

            ArrayList<String> studentsInGroup = studentsGroups.get(groupsKeys[i]);
            for (String student : studentsInGroup) {
                studentPoints.get(groupsKeys[i]).put(student, new ArrayList<>());
            }
            for (int j = 0; j < studentsInGroup.size(); j++) {
                for (int p : points) {
                    studentPoints
                            .get(groupsKeys[i])
                            .get(studentsInGroup.get(j))
                            .add(randomValue(p));
                }
            }
        }

        // Ваш код закінчується тут

        System.out.println("Завдання 2");
        System.out.println(studentPoints.entrySet());

        // Завдання 3
        // Заповніть словник, де:
        // - ключ – назва групи
        // - значення – словник, де:
        //   - ключ – студент, який відносяться до відповідної групи
        //   - значення – сума оцінок студента

        HashMap<String, HashMap<String, Integer>> sumPoints = new HashMap<>();

        // Ваш код починається тут

        for (int i=0;i<groupsKeys.length;i++) {
            if (sumPoints.containsKey(groupsKeys[i]) == false)
                sumPoints.put(groupsKeys[i], new HashMap<>());

            for (String student : studentsGroups.get(groupsKeys[i])) {
                int s = 0;
                ArrayList<Integer> pp = studentPoints.get(groupsKeys[i]).get(student);
                for (int j = 0; j < pp.size(); j++)
                    s += pp.get(j);

                sumPoints.get(groupsKeys[i]).put(student, s);
            }
        }

        // Ваш код закінчується тут

        System.out.println("Завдання 3");
        System.out.println(sumPoints.entrySet());

        // Завдання 4
        // Заповніть словник, де:
        // - ключ – назва групи
        // - значення – середня оцінка всіх студентів групи

        HashMap<String, Float> groupAvg = new HashMap<>();

        // Ваш код починається тут

        for (int i=0;i<groupsKeys.length;i++) {
            int sum = 0, num = 0;
            String[] stud = sumPoints.get(groupsKeys[i]).keySet().toArray(new String[0]);
            for (int j = 0; j < stud.length; j++) {
                num++;
                sum += sumPoints.get(groupsKeys[i]).get(stud[j]);
            }
            groupAvg.put(groupsKeys[i], (float)sum/num);
        }

        // Ваш код закінчується тут

        System.out.println("Завдання 4");
        System.out.println(groupAvg.entrySet());

        // Завдання 5
        // Заповніть словник, де:
        // - ключ – назва групи
        // - значення – масив студентів, які мають >= 60 балів

        HashMap<String, ArrayList<String>> passedPerGroup = new HashMap<>();

        // Ваш код починається тут

        for (int i=0;i<groupsKeys.length;i++) {

            if (!passedPerGroup.containsKey(groupsKeys[i]))
                passedPerGroup.put(groupsKeys[i], new ArrayList<>());

            for (String student : studentsGroups.get(groupsKeys[i])) {
                if (sumPoints.get(groupsKeys[i]).get(student) >= 60){
                    passedPerGroup.get(groupsKeys[i]).add(student);
                }
            }
        }

        // Ваш код закінчується тут

        System.out.println("Завдання 5");
        System.out.println(passedPerGroup.entrySet());

    }

    private static int randomValue(int maxValue){
        Random rand = new Random();
        switch(rand.nextInt(6)) {
            case 1:
                return (int) (maxValue * 0.7);
            case 2:
                return (int) (maxValue * 0.9);
            case 3:
            case 4:
            case 5:
                return maxValue;
            default:
                return 0;
        }



    }
}
