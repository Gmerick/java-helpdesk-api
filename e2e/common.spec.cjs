const { test, expect } = require('@playwright/test');
const errors=[];
test.beforeEach(async ({ page }) => { errors.length=0; page.on('pageerror',e=>errors.push(e.message)); await page.goto('/'); await expect(page.locator('#workspace')).toHaveAttribute('aria-busy','false'); await expect(page.locator('#error')).toBeHidden(); });
test.afterEach(async()=>{expect(errors).toEqual([]);});
test('navegação mobile e formulário acessível por teclado',async({page})=>{
 await page.setViewportSize({width:390,height:844});
 await expect(page.getByRole('heading',{level:1})).toBeVisible();
 expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBeTruthy();
 await page.getByRole('button',{name:'Como usar',exact:true}).click();
 await expect(page.getByRole('dialog')).toBeVisible();await page.keyboard.press('Escape');await expect(page.getByRole('dialog')).not.toBeVisible();
 await page.locator('#create').click();await expect(page.getByRole('dialog')).toBeVisible();
 await page.locator('#edit-form button[type=submit]').click();
 await expect(page.locator('#edit-form :invalid').first()).toBeVisible();
 await page.keyboard.press('Escape');await expect(page.getByRole('dialog')).not.toBeVisible();
});
test('falha de conexão permite tentar novamente',async({page})=>{
 await page.route('**/api/**',route=>route.abort());await page.getByRole('button',{name:'Atualizar',exact:true}).click();
 await expect(page.getByRole('alert')).toContainText('Não foi possível conectar');
 await page.unroute('**/api/**');await page.getByRole('button',{name:'Tentar novamente'}).click();await expect(page.locator('#workspace')).toHaveAttribute('aria-busy','false');await expect(page.locator('#error')).toBeHidden();
});
