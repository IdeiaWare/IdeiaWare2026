package edu.unisc.lic.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

// CASE-FIX/D-5: forca nome de tabela minusculo (MySQL Linux case-sensitive) -- sozinho nao basta, precisa tambem de lower_case_table_names=1.
public class LowerCaseTableNamingStrategy extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = 1L;

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		if (name == null) {
			return null;
		}
		return Identifier.toIdentifier(name.getText().toLowerCase(), name.isQuoted());
	}
}
