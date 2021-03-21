import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String str = in.nextLine();
        if(validation(str)) {
            System.out.println(unpacking(str));
        } else {
            System.out.println("Validation failed.");
        }

    }

    private static boolean validation(String str) {
        int op = 0, cl = 0;
        for (int i = 0; i < str.length(); i++) {    //проверка на соответствие "скобочной структуре"
            char ch = str.charAt(i);
            if (ch == '[') {
                op++;
            }
            if (ch == ']') {
                cl++;
            }
            if (cl > op) {
                return false;
            }
        }
        if (op > cl) {
            return false;
        }
        return validUnit(str);
    }

    private static boolean validUnit(String str) {
        if (str.indexOf('[') == -1) {
            if (!Pattern.matches("[a-zA-Z]*", str)) {
                return false;
            }
            Pattern pattern3 = Pattern.compile("(\\w)\\1+");  //проверка на повторения символов в постподстроке
            Matcher matcher3 = pattern3.matcher(str);
            if (matcher3.find()) {
                return false;
            }
        } else {

            int closing, opening;
            int prevCl = 0, nextOP, op = 0, cl = 0;

            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch == '[') {
                    op++;
                }
                if (ch == ']') {
                    cl++;
                }
                if ((op == cl) && (op != 0)) { //выявление юнита - поиск его окончания

                    closing = i;   //закрывающая ] для юнита
                    if (op == 1) {
                        opening = str.substring(0, closing).lastIndexOf('['); //открывающая [ для юнита: для послед-х
                    } else {
                        opening = prevCl + str.substring(prevCl, closing).indexOf('['); //открывающая [ для юнита: для вложенных
                    }

                    if (str.substring(closing + 1).indexOf('[') != -1) {
                        nextOP = closing + 1 + str.substring(closing + 1).indexOf('[');
                    } else {
                        nextOP = str.length();
                    }

                    //проверка на соответствие шаблону предподстроки + коэф
                    String sub;
                    if (prevCl == 0) {
                        sub = str.substring(prevCl, opening);    //все символы, расположенные до [ в тек. юните
                    } else {
                        sub = str.substring(prevCl + 1, opening);
                    }
                    if (!Pattern.matches("[a-zA-Z]*\\d+", sub)) {
                        return false;
                    } else{
                        Pattern pattern = Pattern.compile("\\d+");
                        Matcher matcher = pattern.matcher(sub);
                        if (Pattern.matches("[a-zA-Z]+", sub)) {   //если есть предподстрока
                            String s = sub.substring(0, matcher.start());   //отделяем предподстроку от коэфа

                            Pattern pattern2 = Pattern.compile("(\\w)\\1+");  //проверка на повторения символов в предподстроке
                            Matcher matcher2 = pattern2.matcher(s);
                            if (matcher2.find()) {
                                return false;
                            }
                        }
                        matcher.find();
                        String k = matcher.group();   //отделяем коэф
                        if(k.charAt(0) == '0'){   //проверка, не начинается ли число на 0
                            return false;
                        }
                    }

                    //проверка на соответствие шаблону постподсроки
                    sub = str.substring(closing + 1, nextOP);   //все символы, расположенные после ] в тек. юните
                    if (!Pattern.matches("[a-zA-Z]*\\d*", sub)) {
                        return false;
                    } else if (Pattern.matches("[a-zA-Z]+", sub)) {    //если есть постподсроки
                        Pattern pattern4 = Pattern.compile("[a-zA-Z]*");
                        Matcher matcher4 = pattern4.matcher(sub);
                        if (matcher4.find()) {
                            Pattern pattern3 = Pattern.compile("(\\w)\\1+");  //проверка на повторения символов в постподстроке
                            Matcher matcher3 = pattern3.matcher(sub.substring(matcher4.start(), matcher4.end()));
                            if (matcher3.find()) {
                                return false;
                            }
                        }
                    }

                    if (!validUnit(str.substring(opening + 1, closing))) {
                        return false;
                    }

                    op = 0;
                    cl = 0;
                    prevCl = i;
                }
            }
        }
        return true;
    }

    private static String unpacking(String str) {
        if (str.indexOf('[') == -1) {
            return str;
        }

        StringBuilder resultStr = new StringBuilder();
        int closing, opening;
        int prevCl = 0, op = 0, cl = 0;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '[') {
                op++;
            }
            if (ch == ']') {
                cl++;
            }
            if ((op == cl) && (op != 0)) { //выявление юнита - поиск его окончания

                closing = i;   //закрывающая ] для юнита
                if (op == 1) {
                    opening = str.substring(0, closing).lastIndexOf('['); //открывающая [ для юнита: для послед-х
                } else {
                    opening = prevCl + str.substring(prevCl, closing).indexOf('['); //открывающая [ для юнита: для вложенных
                }

                //находим коэф k
                String sub = str.substring(prevCl, opening);
                String subK = ""; //ненужное? нач зн
                for (int l = sub.length() - 1; l >= 0; l--) {     //надо идти с конца
                    char c = sub.charAt(l);
                    if ((c >= 48) && (c <= 57)) {
                        subK = c + subK;
                    } else {
                        break;
                    }
                }
                int k = Integer.parseInt(subK);

                //находим предподсроку frontSubS
                String frontSubS = ""; //ненужное нач зн
                if (prevCl == 0) {
                    sub = str.substring(prevCl, opening);
                    for (char c : sub.toCharArray()) {
                        if (((c >= 65) && (c <= 90)) || ((c >= 97) && (c <= 122))) {
                            frontSubS += c;
                        } else {
                            break;
                        }
                    }
                }

                //находим постподсроку postSubS
                sub = str.substring(closing + 1);
                String postSubS = ""; //ненужное нач зн
                for (char c : sub.toCharArray()) {
                    if (((c >= 65) && (c <= 90)) || ((c >= 97) && (c <= 122))) {
                        postSubS += c;
                    } else {
                        break;
                    }
                }

                //присваиваем tempStr выражение внутри [ ]
                String tempStr = unpacking(str.substring(opening + 1, closing));

                //добавление предподстроки
                resultStr.append(frontSubS);
                //повторение выражения по коэф-ту
                resultStr.append(tempStr.repeat(k));
                //добавление постподстроки
                resultStr.append(postSubS);

                op = 0;
                cl = 0;
                prevCl = i;
            }
        }
        return resultStr.toString();
    }
}