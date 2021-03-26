import util.WriteTextToFileHandle;
/*
TODO:
-add ignores/removes for certain things. i.e. the scc20.. for this document.
-change the bullet points to actual latex bullet points
o
have a stack that contains the bullet style points.
if the bullet style is not the same, but is equal to the top of the stack, do not add the end bullet point thing
if the bullet style is not the same, and is not equal to the top of the stack, add the end bullet thing.
when the bullet style changes, and no end is added, add the bullet style to the top of the stack.
when the bullet style changes, and an end is added, remove the bullet style from the top of the stack.
 */
class Main
{
    public static void main(String[] args)
    {
        System.out.println("\n...starting main...");
        RunProgram runner = new RunProgram();
    }


}