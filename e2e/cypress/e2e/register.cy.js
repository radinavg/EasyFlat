describe('register/login page', () => {
  it('when launch application should be able to register or log in', () => {
    cy.visit('/')
    cy.get('[data-cy="not-logged-in-register"]')
    cy.get('[data-cy="not-logged-in-login"]')
  })

  it('should show validation errors when leaving all fields blank', () => {
    cy.visit('#/register')
    cy.get('[data-cy="submit"]').click()
    cy.get('[data-cy="error-first-name"]')
    cy.get('[data-cy="error-last-name"]')
    cy.get('[data-cy="error-email"]')
    cy.get('[data-cy="error-password"]')
    cy.get('[data-cy="error-password-repeat"]')
  })

  it('should show validation error when passwords do not match', () => {
    cy.fixture('settings.json').then((exampleData) => {
      const firstName = exampleData.user.firstName;
      const lastName = exampleData.user.lastName;
      const invalidEmail = exampleData.user.validEmail;
      const password = exampleData.user.password;

      cy.visit('#/register')
      cy.get('[data-cy="first-name-input"]').type(firstName)
      cy.get('[data-cy="last-name-input"]').type(lastName)
      cy.get('[data-cy="email-input"]').type(invalidEmail)
      cy.get('[data-cy="password-input"]').type('a')
      cy.get('[data-cy="password-repeat-input"]').type(password)
      cy.get('[data-cy="submit"]').click()
      cy.get('[data-cy="error-password-repeat"]')
    })
  })
})