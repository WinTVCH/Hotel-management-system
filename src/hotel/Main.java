package hotel;

import javax.swing.JOptionPane;

import hotel.people.Person;
public class Main {
    public static void main () {

    String inputName = JOptionPane.showInputDialog("Enter your name:");
    String inputPhone = JOptionPane.showInputDialog("Enter your phone number:");
    String inputEmail = JOptionPane.showInputDialog("Enter your email address:");

    Person person = new Person(inputName, inputPhone,inputEmail);
    
    JOptionPane.showMessageDialog(null, "Name: " + person.getName() + "\nPhone: " + person.getPhone() + "\nEmail: " + person.getEmail());

}