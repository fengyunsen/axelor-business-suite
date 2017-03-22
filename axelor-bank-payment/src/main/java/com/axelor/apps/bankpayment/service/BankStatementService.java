/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.bankpayment.service;

import java.io.File;
import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.axelor.apps.account.db.repo.MoveRepository;
import com.axelor.apps.account.service.move.MoveLineService;
import com.axelor.apps.account.service.move.MoveService;
import com.axelor.apps.bankpayment.db.BankStatement;
import com.axelor.apps.bankpayment.db.BankStatementFileFormat;
import com.axelor.apps.bankpayment.db.EbicsPartner;
import com.axelor.apps.bankpayment.db.repo.BankReconciliationRepository;
import com.axelor.apps.bankpayment.db.repo.BankStatementRepository;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.google.inject.Inject;

public class BankStatementService {

	protected MoveService moveService;
	protected MoveRepository moveRepository;
	protected MoveLineService moveLineService;
	protected BankReconciliationRepository bankReconciliationRepository;
	
	@Inject
	public BankStatementService(MoveService moveService, MoveRepository moveRepository, MoveLineService moveLineService, BankReconciliationRepository bankReconciliationRepository)  {
		
		this.moveService = moveService;
		this.moveRepository = moveRepository;
		this.moveLineService = moveLineService;
		this.bankReconciliationRepository = bankReconciliationRepository;
	}

	public BankStatement createBankStatement(File file, LocalDate fromDate, LocalDate toDate, 
			BankStatementFileFormat bankStatementFileFormat, EbicsPartner ebicsPartner, LocalDateTime executionDateTime) throws IOException  {
		
		BankStatement bankStatement = new BankStatement();
		bankStatement.setFromDate(fromDate);
		bankStatement.setToDate(toDate);
		bankStatement.setBankStatementFileFormat(bankStatementFileFormat);
		bankStatement.setEbicsPartner(ebicsPartner);
		bankStatement.setGetDateTime(executionDateTime);
		bankStatement.setBankStatementFile(Beans.get(MetaFiles.class).upload(file));
		bankStatement.setName(this.computeName(bankStatement));
		bankStatement.setStatusSelect(BankStatementRepository.STATUS_RECEIVED);
		return bankStatement;
		
	}
	
	private String computeName(BankStatement bankStatement)  {
		
		String name = "";

		if(bankStatement.getEbicsPartner() != null)  {
			name += bankStatement.getEbicsPartner().getPartnerId();
		}
		
		if(bankStatement.getBankStatementFileFormat() != null)  {
			if(name != "")  {  name += "-";  }
			name += bankStatement.getBankStatementFileFormat().getName();
		}
		
		if(bankStatement.getFromDate() != null)  {
			if(name != "")  {  name += "-";  }
			name += bankStatement.getFromDate().toString("YYYY/MM/DD");
		}
		if(bankStatement.getToDate() != null)  {
			if(name != "")  {  name += "-";  }
			name += bankStatement.getToDate().toString("YYYY/MM/DD");
		}
		
		return name;
		
	}
	
	

}
