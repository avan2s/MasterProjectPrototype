package optimization;

public class Simplex {
	// int m Anzahl der Nebenbedingungen
	// int n Anzahl der Entscheidungsbaiablen
	public static double z;
	int primal_simplex(int m, int n, double a[][], double b[], double c[], double x[], int ibv[], double z){
		Simplex.z = z;
		int i,j,r,opt,sp,zp;
		double min;
		
		// Bestimmung einer optimalen Basislösung
		r = 1; // Nummerierung der Basislösungen
		return 0;
	}
	
	public static void basiswechseln(int m, int n, double a[][], double b[], double c[], double x[], int[]ibv, double z, int zp, int sp){
		Simplex.z = z;
		//a = new double[m][n];
		//b = new double[m];
		//c = new double[n];
		//x = new double[n];
		int i,j;
		ibv[zp] = sp; // Variablentausch
		b[zp] = b[zp]/a[zp][sp]; // Pivotzeile
		for(i=0; i<n; i++)
			if(i!=sp) a[zp][i] = a[zp][i]/a[zp][sp];
		a[zp][sp] = 1.0;
		
		for(j=0;j<m;j++){ // Übrige Nebenbedingungen
			if(j != zp){
				b[j] = b[j] - b[zp]*a[j][sp];
				for(i=0;i<n;i++)
					if(i != sp)
						a[j][i] = a[j][i] - a[zp][i]*a[j][sp];
				a[j][sp] = 0.0;
			}
		}
		// Zielfunktionszeile
		for(i=0; i<n;i++){
			if (i != sp)
				c[i] = c[i] - c[sp]*a[zp][i];
		}
		
		Simplex.z = Simplex.z - c[sp]*b[zp]; // Zielfunktionswert
		c[sp] = 0.0;
		
		for(i=0;i<n;i++) x[i] = 0.0; // aktuelle Variablenwerte
		for(j=0; j<m; j++) x[ibv[j]] = b[j];
		return;
	}
}
