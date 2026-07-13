package br.unisc.toolkit.classes;

// TK-VAL: validacoes server-side reutilizaveis (backstop da validacao client-side, burlavel por POST direto).
public class ToolkitValidacao {

	/** true se a string nao for nula/vazia (apos trim) e couber em maxLen. */
	public static boolean textoValido(String s, int maxLen) {
		return s != null && !s.trim().isEmpty() && s.trim().length() <= maxLen;
	}

	/** idade plausivel para uma persona (1..120). */
	public static boolean idadeValida(int age) {
		return age >= 1 && age <= 120;
	}

	/** true se PELO MENOS um dos textos informados for valido (nao vazio). */
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
