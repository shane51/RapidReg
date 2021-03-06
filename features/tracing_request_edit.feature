Feature: Edit tracing request
  As a logged in user, I want to be able to add/view/edit/search tracing requests.

  Background:
    Given I login RapidReg with "primero" account
#    Given I press "login" button
    And I press menu tab "Tracing request"

  Scenario: Edit tracing request through mini form
    When I press "add" button
    And I fill in the following:
      | Inquiry Status                | <Radio> Open                           |
      | Name of inquirer              | Raymond Messiaen                       |
      | Age                           | 42                                     |
      | Nationality                   | <Multiple> Nationality1,Nationality2   |
      | Current Address               | 111 Main St, Davidson NC, 28036 |
      | Is this a permanent location? | <Checkbox> Yes                         |
    And I press "save" button
    Then I should see following:
      | Inquiry Status                | <Radio> Open                    |
      | Name of inquirer              | Raymond Messiaen                |
      | Age                           | 42                              |
      | Nationality                   | Nationality1,Nationality2       |
      | Current Address               | 111 Main St, Davidson NC, 28036 |
      | Is this a permanent location? | <Checkbox> Yes                  |

    When I press "edit" button
    And I edit the value of "Inquiry Status" from "<Radio> Open" to "<Radio> Closed"
    And I edit the value of "Name of inquirer" from "Raymond Messiaen" to "Raymond Justin"
    And I edit the value of "Age" from "42" to "38"
    And I edit the value of "Current Address" from "111 Main St, Davidson NC, 28036" to "111 Main St"
    And I edit the value of "Is this a permanent location?" from "<Checkbox> Yes" to "<Checkbox> No"
    And I press "save" button
    Then I should see following:
      | Inquiry Status                | <Radio> Closed            |
      | Name of inquirer              | Raymond Justin            |
      | Age                           | 38                        |
      | Nationality                   | Nationality1,Nationality2 |
      | Current Address               | 111 Main St               |
      | Is this a permanent location? | <Checkbox> No             |