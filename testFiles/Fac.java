import io;
Class Animal{


public boolean quack(Animal animal){
        io.println("Animal quack");
        return true;
        }
public boolean quack(int var){
        io.println("Ints don't quack");
        return false;
        }
public boolean quack(boolean var){
        io.println("Boolean don't quack");
        return false;
        }
public static void main(String[]args){
        int int_animal;
        boolean bool_animal;
        Animal zebra;
        Animal dolphin;

        zebra=new Animal();
        dolphin=new Animal();
        this.quack(zebra);
        this.quack(dolphin);
        this.quack()
        }


        }