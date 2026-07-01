package edu.unisc.lic.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * TEST-01: testes de logica pura do AES (round-trip), sem banco/container.
 * Garante que criptografar/descriptografar sao inversos e que o texto cifrado
 * difere do original. (Nota de seguranca: a chave AES e fraca/reversivel — ver
 * INFRA-02; estes testes apenas travam o comportamento atual contra regressao.)
 */
public class CriptografaDadosTest {

	@Test
	public void roundTrip_devolveOOriginal() throws Exception {
		String original = "minhaSenha123";
		String cifrado = CriptografaDados.criptografar(original);
		assertNotEquals("o cifrado nao pode ser igual ao texto puro", original, cifrado);
		assertEquals("descriptografar deve devolver o original",
				original, CriptografaDados.descriptografar(cifrado));
	}

	@Test
	public void roundTrip_comAcentosEUnicode() throws Exception {
		// "Acao! cao aei" com acentos via escape unicode (fonte ASCII)
		String original = "Ação! @#% áéí";
		assertEquals(original,
				CriptografaDados.descriptografar(CriptografaDados.criptografar(original)));
	}

	@Test
	public void cifrar_naoVazioEBase64() throws Exception {
		String cifrado = CriptografaDados.criptografar("abc");
		assertNotNull(cifrado);
		assertFalse(cifrado.isEmpty());
		// Base64 padrao so usa [A-Za-z0-9+/=]
		assertEquals(cifrado, cifrado.replaceAll("[^A-Za-z0-9+/=]", ""));
	}
}
