const {test,expect}=require('@playwright/test');
test('texto do usuário é exibido sem executar HTML',async({page,request})=>{
 const title='<img src=x onerror=alert(1)>';
 await request.post('/api/tickets',{data:{title,description:'Teste de conteúdo literal',priority:'LOW'}});
 await page.goto('/');await expect(page.getByRole('row').filter({hasText:title})).toBeVisible();await expect(page.locator('tbody img')).toHaveCount(0);
});
