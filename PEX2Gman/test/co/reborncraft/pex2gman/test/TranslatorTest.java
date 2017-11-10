package co.reborncraft.pex2gman.test;

import co.reborncraft.pex2gman.Pex2Gman;

public class TranslatorTest {
	public static void main(String[] args) {
		String[] cmds = {
				"user ExampleUser",
				"user ExampleUser add example.permission",
				"user ExampleUser remove example.permission",
				"user ExampleUser create",
				"user ExampleUser check example.permission",
				"user ExampleUser list",
				"user ExampleUser prefix ExamplePrefix",
				"user ExampleUser suffix ExamplePrefix",
				"user ExampleUser delete",
				"user ExampleUser group add ExampleGroup",
				"user ExampleUser group remove ExampleGroup",
				"group ExampleGroup add example.permission",
				"group ExampleGroup remove example.permission",
				"group ExampleGroup create",
				"group ExampleGroup check example.permission",
				"group ExampleGroup list",
				"group ExampleGroup prefix ExamplePrefix",
				"group ExampleGroup suffix ExamplePrefix",
				"group ExampleGroup delete",
				"group ExampleGroup user add ExampleUser",
				"group ExampleGroup user remove ExampleUser"
		};
		MockCommandSender mockSender = new MockCommandSender();
		for (String cmd : cmds) {
			System.out.print("/pex " + cmd + ":                      ");
			Pex2Gman.runCommand(mockSender, cmd.split(" "));
		}
	}
}
