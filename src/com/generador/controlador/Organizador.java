package com.generador.controlador;

import java.util.ArrayList;
import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.BooleanGene;
import org.jgap.impl.DefaultConfiguration;

import com.generador.modelo.Materia;
import com.generador.utilidad.MultiMapa;

/**
 * Clase encargada de la optimización del horario
 * 
 * @author Luis Reinoso
 *
 */

public class Organizador {

	private List<Materia> listaMaterias;
	private List<List<Materia>> soluciones;
	private List<IChromosome> listaCromosomas;
	private int numeroMaterias;
	

	public Organizador(MultiMapa mapMaterias) {
		this.listaMaterias = mapMaterias.getAllMaterias();
		this.numeroMaterias = this.listaMaterias.size();
		this.soluciones = new ArrayList<List<Materia>>(5);
		this.listaCromosomas = new ArrayList<IChromosome>();
	}

	public void calcularHorarioOptimo () {

		//Configuracion predeterminada
		Configuration config = new DefaultConfiguration();

		//Almacenar mejor cada generacion
		config.setPreservFittestIndividual(true);

		FitnessFunction fitness = new FitnessHorario(listaMaterias);
		try {
			//Configurar fitness
			config.setFitnessFunction(fitness);

			//Gen de ejemplo

			Gene[] genHorario = new Gene[numeroMaterias];
			for (int i = 0; i < numeroMaterias; i++) {
				genHorario[i] = new BooleanGene(config,false);	
			}

			IChromosome cromosoma = new Chromosome(config, genHorario);
			config.setSampleChromosome(cromosoma);
			config.setPopulationSize(300);

			Genotype poblacion;
			poblacion = Genotype.randomInitialGenotype(config);

			IChromosome cromosomaOptimo;

			//Evolucion
			for (int i = 0; i < 2500; i++) {
				poblacion.evolve();
				cromosomaOptimo = poblacion.getFittestChromosome();
				if (cromosomaOptimo.getFitnessValue() > 700 && !listaCromosomas.contains(cromosomaOptimo))
				{
					listaCromosomas.add(cromosomaOptimo);
					System.out.println(cromosomaOptimo.toString());
					soluciones.add(resultado(cromosomaOptimo));
				}
			}	
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Soluciones obtenidas: "+soluciones.size());
	}

	public String infoMateria (int indice) {
		return listaMaterias.get(indice).toString();
	}

	public List<Materia> resultado (IChromosome cromosomaOptimo) {

		List<Materia> solucion = new ArrayList<Materia>(8);

		for (int i = 0; i < numeroMaterias; i++) {
			if ((boolean) cromosomaOptimo.getGene(i).getAllele()){
				solucion.add(listaMaterias.get(i));
			}
		}

		Integer creditos = 0;
		System.out.println("valor:"+cromosomaOptimo.getFitnessValue());
		
		for (int i = 0; i < numeroMaterias; i++) {
			if ((boolean) cromosomaOptimo.getGene(i).getAllele()) {
				System.out.println(infoMateria(i));
				creditos += listaMaterias.get(i).getIntCreditos();
			}
		}
		
		System.out.println("creditos total: "+creditos);
		return solucion;
	}
}
