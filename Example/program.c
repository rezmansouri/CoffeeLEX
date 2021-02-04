int main()
{
 int i,fact=1,number;
 cout << "Enter a number: ";
  cin >> number;
    for(i=1;i<=number;i++){
      fact=fact*i;
  }
  cout << fact;
return 0;
}