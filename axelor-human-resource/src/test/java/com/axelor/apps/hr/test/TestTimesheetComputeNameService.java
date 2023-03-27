/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2023 Axelor (<http://axelor.com>).
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
package com.axelor.apps.hr.test;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.hr.db.Employee;
import com.axelor.apps.hr.db.Timesheet;
import com.axelor.apps.hr.service.timesheet.TimesheetComputeNameServiceImpl;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestTimesheetComputeNameService {
  protected TimesheetComputeNameServiceImpl timesheetComputeNameService;

  @Before
  public void prepare() {
    timesheetComputeNameService = new TimesheetComputeNameServiceImpl();
  }

  @Test
  public void testComputeEmptyFullName() {
    Timesheet emptyTimesheet = new Timesheet();

    String result = timesheetComputeNameService.computeTimesheetFullname(emptyTimesheet);

    Assert.assertEquals("", result);
  }

  @Test
  public void testComputeFullnameMinimal() {
    Partner contactPartner = createPartner("P0048 - Axelor");
    Employee employee = createEmployee(contactPartner);
    Timesheet timesheet1 = createTimeSheet(employee, null, null);

    String result = timesheetComputeNameService.computeTimesheetFullname(timesheet1);

    Assert.assertEquals("P0048 - Axelor", result);
  }

  @Test
  public void testComputeFullnameWithFromDate() {
    Partner contactPartner = createPartner("P0048 - Axelor");
    Employee employee = createEmployee(contactPartner);
    Timesheet timesheet = createTimeSheet(employee, LocalDate.of(2023, 01, 10), null);

    String result = timesheetComputeNameService.computeTimesheetFullname(timesheet);

    Assert.assertEquals("P0048 - Axelor 10/01/2023", result);
  }

  @Test
  public void testComputeFullnameWithFromDateAndToDate() {
    Partner contactPartner = createPartner("P0048 - Axelor");
    Employee employee3 = createEmployee(contactPartner);
    Timesheet timesheet =
        createTimeSheet(employee3, LocalDate.of(2023, 01, 10), LocalDate.of(2023, 01, 12));

    String result = timesheetComputeNameService.computeTimesheetFullname(timesheet);

    Assert.assertEquals("P0048 - Axelor 10/01/2023-12/01/2023", result);
  }

  protected Partner createPartner(String fullName) {
    Partner contactPartner = new Partner();
    contactPartner.setFullName(fullName);
    return contactPartner;
  }

  protected Employee createEmployee(Partner partner) {
    Employee employee = new Employee();
    employee.setContactPartner(partner);
    return employee;
  }

  protected Timesheet createTimeSheet(Employee employee, LocalDate fromDate, LocalDate toDate) {
    Timesheet timesheet = new Timesheet();
    if (employee != null) {
      timesheet.setEmployee(employee);
    }
    if (fromDate != null) {
      timesheet.setFromDate(fromDate);
    }
    if (toDate != null) {
      timesheet.setToDate(toDate);
    }
    return timesheet;
  }
}