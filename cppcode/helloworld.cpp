#include <iostream>

using namespace std;

int main()
{
    cout << "Hello World 2" << endl;
    cout << " 4 / 5 =" << (float) 4/5 << endl;

    int num[5] = {2,4,5,6,7};
    cout << "num 1: " << num[0] << endl;
    string yourname;
    getline(cin, yourname);
    cout << "hello " << yourname << endl;
    return 0;
}