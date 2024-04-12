import java.util.Scanner;
import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;

abstract class TreeNode {
    abstract void preOrder(); 
    abstract void inOrder();  
    abstract void postOrder(); 
    abstract float evaluate(); 
}

class OperandNode extends TreeNode {
    float value; 

    public OperandNode(float value) {
        this.value = value;
    }

    void preOrder() {
        System.out.print(value + " ");
    }

    void inOrder() {
        System.out.print(value + " ");
    }

    void postOrder() {
        System.out.print(value + " ");
    }

    float evaluate() {
        return value; 
    }
}

class OperatorNode extends TreeNode {
    char operator; 
    TreeNode left, right; 

    public OperatorNode(char operator, TreeNode left, TreeNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    void preOrder() {
        System.out.print(operator + " ");
        left.preOrder();
        right.preOrder();
    }

    void inOrder() {
        left.inOrder();
        System.out.print(operator + " ");
        right.inOrder();
    }

    void postOrder() {
        left.postOrder();
        right.postOrder();
        System.out.print(operator + " ");
    }

    float evaluate() {
        float leftValue = left.evaluate();
        float rightValue = right.evaluate();

        switch (operator) {
            case '+':
                return leftValue + rightValue;
            case '-':
                return leftValue - rightValue;
            case '*':
                return leftValue * rightValue;
            case '/':
                if (rightValue == 0)
                    throw new ArithmeticException("Erro: Divisao por zero");
                return leftValue / rightValue;
            default:
                return Float.NaN; 
        }
    }
}

class ExpressionTree {
    TreeNode root;

    boolean isOperator(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/');
    }

    int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return -1;
    }

    void buildTree(String expression) {
        Stack<TreeNode> nodeStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ')
                continue;

            if (!isOperator(c) && c != '(' && c != ')') {
                StringBuilder operand = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    operand.append(expression.charAt(i));
                    i++;
                }
                i--;
                float value = Float.parseFloat(operand.toString());
                OperandNode operandNode = new OperandNode(value);
                nodeStack.push(operandNode);
            } else {
                if (c == '(') {
                    operatorStack.push(c);
                } else if (c == ')') {
                    while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                        char op = operatorStack.pop();
                        TreeNode right = nodeStack.pop();
                        TreeNode left = nodeStack.pop();
                        OperatorNode operatorNode = new OperatorNode(op, left, right);
                        nodeStack.push(operatorNode);
                    }
                    operatorStack.pop(); 
                } else {
                    while (!operatorStack.empty() && precedence(operatorStack.peek()) >= precedence(c)) {
                        char op = operatorStack.pop();
                        TreeNode right = nodeStack.pop();
                        TreeNode left = nodeStack.pop();
                        OperatorNode operatorNode = new OperatorNode(op, left, right);
                        nodeStack.push(operatorNode);
                    }
                    operatorStack.push(c);
                }
            }
        }

        while (!operatorStack.empty()) {
            char op = operatorStack.pop();
            TreeNode right = nodeStack.pop();
            TreeNode left = nodeStack.pop();
            OperatorNode operatorNode = new OperatorNode(op, left, right);
            nodeStack.push(operatorNode);
        }

        root = nodeStack.peek();
    }

    void preOrder() {
        if (root != null) {
            root.preOrder();
        }
    }

    void inOrder() {
        if (root != null) {
            root.inOrder();
        }
    }

    void postOrder() {
        if (root != null) {
            root.postOrder();
        }
    }

    float evaluateExpression() {
        if (root == null)
            return 0;
        return root.evaluate();
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpressionTree expressionTree = new ExpressionTree();
        boolean expressionEntered = false;
        String infixExpression = "";

        try {
            File menuFile = new File("Menu.txt");
            Scanner fileScanner = new Scanner(menuFile);

            while (fileScanner.hasNextLine()) {
                System.out.println(fileScanner.nextLine());
            }

            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo do menu nao encontrado. Usando menu padrao.");
            showDefaultMenu();
        }

        while (true) {
            System.out.print("\nEscolha uma opcao: ");
            int choice = scanner.nextInt();
            System.out.println("\n");

            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.print("Insira a expressao aritmetica na notacao infixa: ");
                    infixExpression = scanner.nextLine();
                    boolean validExpression = validateExpression(infixExpression);
                    if (validExpression) {
                        System.out.println("Expressao valida.");
                        expressionEntered = true;
                    } else {
                        System.out.println("Expressao invalida.");
                        expressionEntered = false;
                    }
                    break;
                case 2:
                    if (expressionEntered) {
                        expressionTree.buildTree(infixExpression);
                        System.out.println("Arvore de expressao binaria criada.");
                    } else {
                        System.out.println("Insira uma expressao valida primeiro.");
                    }
                    break;
                case 3:
                    if (expressionTree.root != null) {
                        System.out.println("\nArvore em Pre-ordem:");
                        expressionTree.preOrder();
                        System.out.println("\n\nArvore em Ordem:");
                        expressionTree.inOrder();
                        System.out.println("\n\nArvore em Pos-ordem:");
                        expressionTree.postOrder();
                    } else {
                        System.out.println("Crie a arvore primeiro.");
                    }
                    break;
                case 4:
                    if (expressionTree.root != null) {
                        float result = expressionTree.evaluateExpression();
                        System.out.println("\n\nResultado da expressao: " + result);
                    } else {
                        System.out.println("Crie a arvore primeiro.");
                    }
                    break;
                case 5:
                    System.out.println("Encerrando o programa...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opcao invalida. Por favor escolha uma opcao valida.");
            }
        }
    }

    public static void showDefaultMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Entrada da expressao aritmetica na notacao infixa.");
        System.out.println("2. Criacao da arvore binaria de expressao aritmetica.");
        System.out.println("3. Exibicao da arvore binaria de expressao aritmetica.");
        System.out.println("4. Calculo da expressao (realizando o percurso da arvore).");
        System.out.println("5. Encerramento do programa.");
    }

    public static boolean validateExpression(String expression) {
        boolean validCharacters = expression.matches("^[0-9+\\-*/().\\s]+$");
        boolean balancedParentheses = balancedParentheses(expression);
        boolean validOperators = validOperators(expression);
        return validCharacters && balancedParentheses && validOperators;
    }

    private static boolean balancedParentheses(String expression) {
        int counter = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(')
                counter++;
            else if (c == ')')
                counter--;
            if (counter < 0)
                return false;
        }
        return counter == 0;
    }

    private static boolean validOperators(String expression) {
        if (expression.matches(".[+\\-/]{2,}.*")) {
            return false;
        }
        if (expression.matches("^[+\\-/].|.[+\\-/]$")) {
            return false;
        }
        return !expression.matches(".[^0-9()+\\-/.\\s].*");
    }
}