package br.unisc.toolkit.classes;

// TK-VAL: validacoes server-side reutilizaveis (backstop do client-side).
public class ToolkitValidacao {

	public static boolean textoValido(String s, int maxLen) {
		return s != null && !s.trim().isEmpty() && s.trim().length() <= maxLen;
	}

	public static boolean idadeValida(int age) {
		return age >= 1 && age <= 120;
	}

	public static boolean algumTextoValido(String... textos) {
		if (textos == null) {
			return false;
		}
		for (String t : textos) {
			if (t != null && !t.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}
}
