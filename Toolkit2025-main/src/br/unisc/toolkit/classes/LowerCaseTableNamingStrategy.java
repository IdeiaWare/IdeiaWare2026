package br.unisc.toolkit.classes;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

// CASE-FIX: forca o nome fisico da tabela pra minusculo (entidades sem @Table herdam o nome
// da classe com maiuscula, que quebra no MySQL Linux case-sensitive). Ver D-5 no relatorio.
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
